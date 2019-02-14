package com.unclecabby.unclecabby;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.unclecabby.unclecabby.Common.Common;
import com.unclecabby.unclecabby.Model.Rating;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RateActivity extends AppCompatActivity {

    Button btnSubmit;
    MaterialEditText edtComment;
    MaterialRatingBar ratingBar;

    FirebaseDatabase database;
    DatabaseReference rateDetailRef;
    DatabaseReference driverInformationRef;

    double ratingStars=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        //firebase
        database =FirebaseDatabase.getInstance();
        rateDetailRef=database.getReference(Common.rating_detail_tbl);

        btnSubmit =(Button)findViewById(R.id.btnSubmit);
        ratingBar= (MaterialRatingBar)findViewById(R.id.ratingBar);
        edtComment =(MaterialEditText)findViewById(R.id.edtComment);

        ratingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                ratingStars=rating;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRatingDetails(Common.driverId);
            }
        });

    }

    private void submitRatingDetails(final String driverId) {
        final android.app.AlertDialog alertDialog = new SpotsDialog(this);
        alertDialog.show();

        Rating rating = new Rating();
        rating.setRatings(String.valueOf(ratingStars));
        rating.setComments(edtComment.getText().toString());

        //update new values to firebase
        rateDetailRef.child(driverId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        rateDetailRef.child(driverId)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        double averageStars= 0.0;
                                        int count=0;
                                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                        {
                                            Rating rating = postSnapshot.getValue(Rating.class);
                                            averageStars += Double.parseDouble(rating.getRatings());
                                            count++;
                                        }
                                        final double finalAverage=averageStars/count;
                                        DecimalFormat df = new DecimalFormat("#.#");
                                        String valueUpdate = df.format(finalAverage);

                                        Map<String,Object>driverUpdateRatings= new HashMap<>();
                                        driverUpdateRatings.put("ratings",valueUpdate);

                                        driverInformationRef.child(Common.driverId)
                                                .updateChildren(driverUpdateRatings)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        alertDialog.dismiss();
                                                        Toast.makeText(RateActivity.this, "Thanks for Feedback", Toast.LENGTH_SHORT).show();
                                                        finish();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        alertDialog.dismiss();
                                                        Toast.makeText(RateActivity.this, "Ratings updated but can't write to Driver Information", Toast.LENGTH_SHORT).show();
                                                    }
                                                });



                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    alertDialog.dismiss();
                        Toast.makeText(RateActivity.this,"Rating Failed !",Toast.LENGTH_SHORT).show();
                    }
                });


    }
}
