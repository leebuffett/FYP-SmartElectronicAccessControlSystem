package com.example.smartelectronicaccesscontrolsystem.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartelectronicaccesscontrolsystem.Model.staff;
import com.example.smartelectronicaccesscontrolsystem.R;
import com.example.smartelectronicaccesscontrolsystem.add_edit_staff;
import com.example.smartelectronicaccesscontrolsystem.delete_staff;
import com.example.smartelectronicaccesscontrolsystem.Model.staff;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder>{

    private Context mcontext;
    // //  private List<Notification> mnotify;
    private List<staff> allStaff;
    private String staffActivity;

//    //storage
//    FirebaseStorage storage = FirebaseStorage.getInstance();
//    StorageReference storageReference;

    public StaffAdapter(Context mcontext,List<staff> allStaff, String staffActivity){
        this.mcontext=mcontext;
        this.allStaff=allStaff;
        this.staffActivity=staffActivity;
    }

    @NonNull
    @Override
    public StaffAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.list_of_the_staff,parent,false);

        return new StaffAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final StaffAdapter.ViewHolder holder, int position) {

        final staff stafflist = allStaff.get(position);

        Picasso.get().load(stafflist.getImage()).into(holder.staffimg);
        holder.staffName.setText(stafflist.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (staffActivity.equals("edit")) {
                    Intent i = new Intent(mcontext, add_edit_staff.class);
                    i.putExtra("staffId", stafflist.getId());
                    i.putExtra("name", stafflist.getName());
                    i.putExtra("phone", stafflist.getPhoneNumber());
                    i.putExtra("cc", stafflist.getCountryCode());
                    i.putExtra("bod", stafflist.getDateOfBirth());
                    i.putExtra("age", stafflist.getAge());
                    i.putExtra("gender", stafflist.getGender());
                    i.putExtra("email", stafflist.getEmail());
                    i.putExtra("image", stafflist.getImage());
                    i.putExtra("activity", "editStaff");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.startActivity(i);
                }
                else if (staffActivity.equals("delete")) {
                    Intent i = new Intent(mcontext, delete_staff.class);
                    i.putExtra("staffId", stafflist.getId());
                    i.putExtra("name", stafflist.getName());
                    i.putExtra("phone", stafflist.getPhoneNumber());
                    i.putExtra("cc", stafflist.getCountryCode());
                    i.putExtra("bod", stafflist.getDateOfBirth());
                    i.putExtra("age", stafflist.getAge());
                    i.putExtra("gender", stafflist.getGender());
                    i.putExtra("email", stafflist.getEmail());
                    i.putExtra("image", stafflist.getImage());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return allStaff.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView staffName;
        CircleImageView staffimg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            staffName = (TextView) itemView.findViewById(R.id.staff_name);
            staffimg = (CircleImageView) itemView.findViewById(R.id.notification_image);

        }
    }
}
