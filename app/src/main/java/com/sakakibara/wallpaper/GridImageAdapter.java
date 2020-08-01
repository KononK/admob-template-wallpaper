package com.sakakibara.wallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class GridImageAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<Integer> imageUrls;

    GridImageAdapter(Context context, List<Integer> imgDetails) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        imageUrls = imgDetails;
    }

    public int getCount() {
        return imageUrls.size()+MainActivity.ADDED_EXTRA;
    }

    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (position < MainActivity.ADDED_EXTRA / 2) {
            position = imageUrls.size()- (MainActivity.ADDED_EXTRA / 2 - position);
        } else if (position >= (imageUrls.size() + (MainActivity.ADDED_EXTRA / 2))) {
            position = position
                    - (imageUrls.size() + (MainActivity.ADDED_EXTRA / 2));
        } else {
            position -= (MainActivity.ADDED_EXTRA / 2);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_image, parent, false);
        }

        imageView = convertView.findViewById(R.id.gridview_image);

        try{
            imageView.setImageBitmap(parseImage(imageUrls.get(position)));
            imageView.setTag(""+position);
        }catch(Exception e){
            e.printStackTrace();
        }

        return convertView;
    }

    private Bitmap parseImage(int url) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), url);
            Bitmap bitmap = Bitmap.createScaledBitmap(bmp, 100, 100, true);
            bmp.recycle();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
