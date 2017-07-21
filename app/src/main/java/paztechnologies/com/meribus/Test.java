package paztechnologies.com.meribus;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Admin on 7/18/2017.
 */

public class Test implements RecyclerView.OnItemTouchListener {

    public interface OnItemClickListner
    {
        public void onItemClick(View view,int postion);

    }
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
