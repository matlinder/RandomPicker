package linderlabs.randompicker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NearbyActivity extends AppCompatActivity implements View.OnClickListener {
    double longitudeD, latitudeD;
    private ProgressDialog prgDialog; //dialog
    private SeekBar seekBar;
    private TextView radiusText;
    private EditText keyword;
    int radius = 1;
    private Button typeButton, price1, price2, price3, price4, btn_unfocus;
    private String type = null;
    private int price = 0;
    private String[] types = {"restaurant","meal_delivery","meal_takeaway","amusement_park", "aquarium","art_gallery", "bakery", "bar", "beauty_salon", "bicycle_store",
            "book_store","bowling_alley","cafe","campground","casino","clothing_store","convenience_store","electronics_store",
            "gym","library","liquor_store","lodging",
            "movie_theater","museum","night_club","park","shopping_mall","spa","stadium","store","supermarket",
            "zoo","No Type"};
    private String[] prettyTypes = {"Restaurant","Food Delivery","Food Take out/To Go","Amusement Park", "Aquarium","Art Gallery", "Bakery", "Bar", "Salon", "Cycling Store",
            "Book Store","Bowling","Cafe","Campground","Casino","Clothing Store","Convenience Store","Electronics Store",
            "Gym","Library","Liquor Store","Lodging",
            "Theater","Museum","Night Club","Park", "Mall","Spa","Stadium","Store","Supermarket",
            "Zoo", "No Type"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Nearby");

        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        seekBar = findViewById(R.id.seekBarRadius);
        radiusText = findViewById(R.id.seekNum);
        typeButton = findViewById(R.id.typeButton);
        price1 = findViewById(R.id.price1);
        price2 = findViewById(R.id.price2);
        price3 = findViewById(R.id.price3);
        price4 = findViewById(R.id.price4);
        price1.setOnClickListener(this);
        price2.setOnClickListener(this);
        price3.setOnClickListener(this);
        price4.setOnClickListener(this);
        keyword = findViewById(R.id.editKeyword);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progressValue += 1;
                radiusText.setText(String.valueOf(progressValue) + " KM");
                radius = progressValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Writing to storage permission has not been granted.
            requestGPSPermission();

        } else {
//            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                longitudeD = location.getLongitude();
//                                longitudeT.setText(String.valueOf(longitudeD));
                                latitudeD = location.getLatitude();
//                                latitudeT.setText(String.valueOf(latitudeD));
                                Toast.makeText(getApplicationContext(), "we made it", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                displayLocationSettingsRequest(getApplicationContext());
                            }
                        }
                    });

        }

    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:


                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(NearbyActivity.this, 0x1);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("nearby", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("nearby", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }
    private void requestGPSPermission() {
        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Camera permission has not been granted yet. Request it directly.
            showExplanation("Permission Needed to Use Location", "This permission is needed to use your location in order to access the nearby places from Google, no information is sent to anyone.", Manifest.permission.ACCESS_FINE_LOCATION, 1);

        }else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        // END_INCLUDE(camera_permission_request)
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == 1) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
//            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();
            }else
            {
                Toast.makeText(getApplicationContext(), "Write permission denied, cannot save", Toast.LENGTH_LONG).show();
            }

            // END_INCLUDE(permission_result)

        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }
    public void getNearbyPlaces(View view) {
        String _keyword = keyword.getText().toString();
        if(_keyword != null && _keyword.length() > 0)
        {
            //params
        }
        if(type != null && !type.equals("No Type") )
        {
            //params
        }

    }
//    public void displayNetworkData() {
//        prgDialog.show();
//        AsyncHttpClient client = new AsyncHttpClient();
//
//        StringBuilder googlePlacesUrl =
//                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//                googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
//                googlePlacesUrl.append("&radius=").append(PROXIMITY_RADIUS);
//                googlePlacesUrl.append("&types=").append(type);
//                googlePlacesUrl.append("&sensor=true");
//                googlePlacesUrl.append("&key=" + GOOGLE_BROWSER_API_KEY);
//
//        client.get(googlePlacesUrl.toString(), new AsyncHttpResponseHandler() {
//
//            public void onSuccess(String response) {
//                try {
//                    prgDialog.hide();
//                    JSONObject obj = new JSONObject(response);
//                    JSONArray objArray = obj.getJSONArray("Result");
//
//                    for (int i = 0; i < objArray.length(); i++) {
//
//
//                    }
//
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    Toast.makeText(getApplicationContext(), "message: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                }
//            }
//
//            public void onFailure(int statusCode, Throwable error, String content) {
//                prgDialog.hide();
//                // When Http response code is '404'
//                if (statusCode == 404) {
//                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
//                }
//                // When Http response code is '500'
//                else if (statusCode == 500) {
//                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
//                }
//                // When Http response code other than 404, 500
//                else {
//                    Toast.makeText(getApplicationContext(), "Unexpected Error occurred! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//
//    }

    public void displayTypesList(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select A Type of Place");

        builder.setItems(prettyTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                typeButton.setText(prettyTypes[which]);
                type = types[which];


            }
        });
        builder.show();
    }
    public void priceOnClick(View view) {

    }


    /**
     * Clicking the back button on the title bar returns to the previous activity on the stack
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                prgDialog.dismiss();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method for back button on title bar
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * What to do when the acitivty is destroyed
     */
    public void onDestroy() {
        prgDialog.dismiss();
        super.onDestroy();
        finish();
    }

    /**
     * what to do when the activity is paused
     */
    public void onPause()
    {
        prgDialog.dismiss();
        super.onPause();

    }

    /**
     * what to do when the activity is resumed
     */
    public void onResume()
    {
        super.onResume();
    }


    public void cancel(View view) {

        prgDialog.dismiss();
        finish();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.price1 :

                setFocus(btn_unfocus, price1);
                price = 1;
                break;

            case R.id.price2 :
                setFocus(btn_unfocus, price2);
                price = 2;
                break;

            case R.id.price3 :
                setFocus(btn_unfocus, price3);
                price = 3;
                break;

            case R.id.price4 :
                setFocus(btn_unfocus, price4);
                price = 4;
                break;
        }
    }

    private void setFocus(Button btn_unfocus, Button btn_focus){

        if(this.btn_unfocus == btn_focus && !btn_focus.getBackground().equals(R.drawable.mybutton))
        {
            btn_focus.setBackgroundResource(R.drawable.mybutton);
            price = 0;
            this.btn_unfocus = null;
            return;
        }
        if(btn_unfocus != null) {
            btn_unfocus.setBackgroundResource(R.drawable.mybutton);
        }
        btn_focus.setBackgroundResource(R.drawable.mybutton_pressed);
        this.btn_unfocus = btn_focus;


    }
}
