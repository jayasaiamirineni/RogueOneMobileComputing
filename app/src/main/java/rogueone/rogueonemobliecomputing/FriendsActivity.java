package rogueone.rogueonemobliecomputing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import rogueone.rogueonemobliecomputing.Interfaces.APIClient;
import rogueone.rogueonemobliecomputing.Interfaces.ServiceGenerator;

public class FriendsActivity extends OptionsMenuActivity {
    ProgressDialog progressDialog;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @BindView(R.id.my_friends)
    RecyclerView _recyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
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
        progressDialog = new ProgressDialog(FriendsActivity.this,
                R.style.AppTheme_Dark_Dialog);
        ButterKnife.bind(this);
        List<String> friends = (List<String>)getIntent().getSerializableExtra("friendList");
        mLayoutManager = new LinearLayoutManager(this);
        _recyView.setLayoutManager(mLayoutManager);
        mAdapter = new FriendsAdapter(friends,client,getApplicationContext());
        _recyView.setAdapter(mAdapter);
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
