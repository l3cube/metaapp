package com.pict.metaappui.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.pict.metaappui.R;
import com.pict.metaappui.util.Preferences;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class Homepage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    NavigationView navigationView;
    Toolbar toolbar;
    View navHeader;

    //navheader components
    CircleImageView userProfileImage;
    TextView userProfileName;
    TextView userProfileEmail;

    String userName;
    String userEmail;
    Uri userPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        //set initial fragment
        Bundle bundle=new Bundle();
        bundle.putString("Category","all");
        bundle.putInt("Level",1);
        bundle.putString("TLC", "null");
        Level1_Categories fragment = new Level1_Categories();
        fragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeader=navigationView.getHeaderView(0);
        userProfileImage=(CircleImageView)navHeader.findViewById(R.id.profile_image);
        userProfileName=(TextView)navHeader.findViewById(R.id.username);
        userProfileEmail=(TextView)navHeader.findViewById(R.id.email);
        //Update navheader depending on login state
        userName=Preferences.getString(Preferences.USER_NAME);
        if(userName==null){
            userProfileName.setText("User Name");
            userProfileEmail.setText("user@host.com");
            userProfileImage.setImageResource(R.drawable.defaultprofilephoto);
        }
        else{
            userEmail=Preferences.getString(Preferences.USER_EMAIL);
            userPhoto=Uri.parse(Preferences.getString(Preferences.USER_PHOTO));
            userProfileName.setText(userName);
            userProfileEmail.setText(userEmail);
            try {
                userProfileImage.setImageBitmap(getThumbnail(userPhoto));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent=new Intent(getApplicationContext(), Login.class);
                startActivity(loginIntent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homepage, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_category) {
            //set initial fragment
            Bundle bundle=new Bundle();
            bundle.putString("Category","all");
            bundle.putInt("Level",1);
            Level1_Categories fragment = new Level1_Categories();
            fragment.setArguments(bundle);
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_offer) {
            //set initial fragment
            Offer_received fragment = new Offer_received();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_namespace) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Cannot access UI elements of Nav drawer header?
    public void setUserData() {
        de.hdodenhof.circleimageview.CircleImageView profile;
        profile = (CircleImageView) findViewById(R.id.profile_image);
        profile.setImageResource(R.drawable.profile);

        TextView name;
        name=(TextView)findViewById(R.id.username);
        name.setText("Tushar Badgu");

        TextView email;
        email=(TextView)findViewById(R.id.email);
        email.setText("tushar.badgu@gmail.com");
    }

    //Load bitmap from uri
    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        int THUMBNAIL_SIZE=64;
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither=true;//optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

    @Override
    public void onResume(){
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

}
