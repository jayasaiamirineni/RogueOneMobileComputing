package rogueone.rogueonemobliecomputing;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.Chip;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rogueone.rogueonemobliecomputing.Interfaces.APIClient;
import rogueone.rogueonemobliecomputing.Interfaces.ServiceGenerator;
import rogueone.rogueonemobliecomputing.Models.LocationEntry;
import rogueone.rogueonemobliecomputing.Models.MyLocation;

public class LocationEntryActivity extends OptionsMenuActivity {

    ProgressDialog progressDialog;
    MyLocation location;
    String address;
    List<String> tripList;
    @BindView(R.id.friendly_location)
    TextView _location;
    @BindView(R.id.trips_spinner)
    Spinner _trips;
    @BindView(R.id.tags)
    ChipsInput _tags;
    @BindView(R.id.comment)
    TextView _comment;
    @BindView(R.id.add_entry)
    Button _addEntry;

    public View.OnClickListener sendEntry = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startDialog();
            LocationEntry loc = getLocationFromScreen();
            Call<ResponseBody> call = client.createEntry(loc);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        closeDialog();
                        Toast.makeText(getApplicationContext(),"Entry Successfully Created",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtra("token",token);
                        startActivity(intent);
                    }else{
                        closeDialog();
                        try {
                            showErrorToast(new Throwable(response.errorBody().string()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    closeDialog();
                    showErrorToast(t);
                }
            });
        }
    };

    private LocationEntry getLocationFromScreen() {
        LocationEntry entry = new LocationEntry();
        entry.Address = _location.getText().toString();
        entry.Latitude = location.latitude;
        entry.Longitude = location.longitude;
        List<Chip> chips = (List<Chip>) _tags.getSelectedChipList();
        List<String> badges = new ArrayList<>();
        for(Chip chip:chips){
            badges.add(chip.getLabel());
        }
        entry.BadgeNames = badges;
        entry.tripName = _trips.getSelectedItem()==null?null:_trips.getSelectedItem().toString();
        entry.Comments = _comment.getText().toString();
        return entry;
    }

    public View.OnClickListener launchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String uri = String.format(Locale.getDefault(),"geo:%f,%f",location.latitude,location.longitude);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_entry);
        try{
            token = getIntent().getStringExtra("token");
        }catch(Exception e){
            Log.e(android.support.compat.BuildConfig.APPLICATION_ID,e.getMessage());
        }
        if(token==null){
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
        client = ServiceGenerator
                .createService(APIClient.class,token,getBaseContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbarMain);
        setTitle("LODI");
        setSupportActionBar(toolbar);
        TextView mTitle = (TextView) toolbar.getChildAt(0);
        mTitle.setOnClickListener(homeListener);
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Lobster-Regular.ttf");
        mTitle.setTypeface(tf);
        progressDialog = new ProgressDialog(LocationEntryActivity.this,
                R.style.AppTheme_Dark_Dialog);
        ButterKnife.bind(this);
        address = getIntent().getStringExtra("Address");
        location = (MyLocation) getIntent().getSerializableExtra("location");
        if(address!=null&&address!=""){
            _location.setText(address);
        }else{
            _location.setText("latitude : "+location.latitude+"\n longitude : "+location.longitude);
        }
        tripList = (List<String>) getIntent().getSerializableExtra("tripList");
        if(tripList==null||tripList.size()==0){
            _trips.setVisibility(View.INVISIBLE);
        }else{
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_item,tripList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            _trips.setAdapter(adapter);
        }
        setChips();
        _location.setOnClickListener(launchListener);
        _addEntry.setOnClickListener(sendEntry);
    }

    private void setChips() {
        List<Chip> chips = new ArrayList<>() ;
        chips.add(new Chip("barber",null));
        chips.add(new Chip("Grocery",null));
        chips.add(new Chip("Market",null));
        chips.add(new Chip("SportComplex",null));
        chips.add(new Chip("Gym",null));
        chips.add(new Chip("School",null));
        _tags.setFilterableList(chips);
    }

    @Override
    public void startDialog(){
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }
    @Override
    public void closeDialog(){
        progressDialog.dismiss();
    }
    @Override
    public void showErrorToast(Throwable t){
        Toast.makeText(getApplicationContext(),t.getLocalizedMessage(),Toast.LENGTH_LONG).show();
    }
}
