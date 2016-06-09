package letseeitcrash.android.controllincameraapp;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements CameraFragment.OnCameraFragmentInteractionListener, PreviewFragment.OnPreviewFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*inicia fragment*/
        CameraFragment cam_fragment = CameraFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); //getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, cam_fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCameraFragmentInteraction(Uri uri) {
        /*preview*/
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        PreviewFragment prev_fragment = PreviewFragment.newInstance(uri.toString());
        //transaction = getSupportFragmentManager().beginTransaction(); //getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, prev_fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onPreviewFragmentInteraction(Uri uri) {
        //
    }
}
