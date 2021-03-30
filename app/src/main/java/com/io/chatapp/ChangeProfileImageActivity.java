package com.io.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.io.chatapp.Model.MyToast;
import com.io.chatapp.Prevalent.Prevalent;
import com.theartofdev.edmodo.cropper.CropImage;
import java.util.HashMap;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;
import static com.io.chatapp.Utils.glideOptions;

public class ChangeProfileImageActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private ImageView changeBtn;
    private Uri imageUri;
    private MyToast myToast;
    private StorageReference storageProfilePictureRef;
    private StorageTask uploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Change Profile Image");
        profileImage = (CircleImageView) findViewById(R.id.change_profile_image);
        changeBtn = (ImageView) findViewById(R.id.change_btn);
        myToast = new MyToast(ChangeProfileImageActivity.this);
        storageProfilePictureRef =  FirebaseStorage.getInstance().getReference().child("ProfileImages");
        if (Prevalent.currentOnlineUser!=null){
            Glide.with(ChangeProfileImageActivity.this).load(Prevalent.currentOnlineUser.getImage()).apply(glideOptions).into(profileImage);
        }
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkAvailable(getApplication())){
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(ChangeProfileImageActivity.this);
                }
                else{
                    myToast.show("The network is not available.");
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            Intent intent = new Intent(ChangeProfileImageActivity.this,HomeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            uploadImage();
        }
        else{
            myToast.show("Error, please try again.");
            Intent intent = new Intent(ChangeProfileImageActivity.this,ChangeProfileImageActivity.class);
            startActivity(intent);
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Upload the image.");
        progressDialog.setMessage("Plase wait.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri==null){
            progressDialog.dismiss();
            myToast.show("The upload task failed.");
        }
        else {
            final StorageReference fileRef = storageProfilePictureRef
                    .child(Prevalent.currentOnlineUser.getUid() + "jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(!task.isSuccessful()){
                        progressDialog.dismiss();
                        myToast.show("The upload task failed.");
                    }
                    else{
                        Uri downloadUrl = (Uri) task.getResult();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("image",downloadUrl.toString());
                        ref.child(Prevalent.currentOnlineUser.getUid()).updateChildren(userMap);
                        Prevalent.currentOnlineUser.setImage(downloadUrl.toString());
                        Glide.with(ChangeProfileImageActivity.this).load(downloadUrl.toString()).apply(glideOptions).into(profileImage);
                        progressDialog.dismiss();
                        myToast.show("Upload profile image successfully.");
                    }
                }

            });
        }
    }

}