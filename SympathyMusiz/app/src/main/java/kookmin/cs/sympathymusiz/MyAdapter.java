package kookmin.cs.sympathymusiz;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
	
    private ArrayList<String> array;	//Arraylist for managing a file list
    private Context ctx;

    public MyAdapter(Context ctx) {
        this.ctx = ctx;
        array = new ArrayList<String>();
    }
    
 
    @Override
    public int getCount() {
        return this.array.size();
    }
    
    @Override
    public Object getItem(int position) {
        return this.array.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    
    //add a String Item in a List
    public void addItem(String item) {
        this.array.add(item);
        this.notifyDataSetChanged();
    }

   
    // Delete an Item from a List
    public void clearItem() {
        this.array.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View newView;

        if (convertView == null) {
            newView = View.inflate(ctx, R.layout.item_layout, null);
        } else {
            newView = convertView;
        }

        String txt = (String) this.getItem(position);
        
        //Numbering listview
        ((TextView) newView.findViewById(R.id.item_numb)).setText("  "
                + (position + 1) + ". "+txt);

        return newView;
    }
    public ArrayList<String> getArr(){
       return array;
    }
    
    public void recovery(ArrayList<String> arr){
       this.array=arr;
       
    }

}