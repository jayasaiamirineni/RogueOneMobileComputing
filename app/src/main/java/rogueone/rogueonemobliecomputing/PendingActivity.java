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

public class PendingActivity extends OptionsMenuActivity {
    ProgressDialog progressDialog;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @BindView(R.id.pending_requests)
    RecyclerView _recyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);
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
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Lobster-Regular.ttf");
        mTitle.setTypeface(tf);
        progressDialog = new ProgressDialog(PendingActivity.this,
                R.style.AppTheme_Dark_Dialog);
        ButterKnife.bind(this);
        List<String> requests = (List<String>)getIntent().getSerializableExtra("requestList");
        mLayoutManager = new LinearLayoutManager(this);
        _recyView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(requests,client,getApplicationContext());
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
