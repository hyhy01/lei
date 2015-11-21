package com.example.lei;

import android.R.color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import asia.jeremie.Core;
import asia.jeremie.Flag;

public class MainActivity extends Activity {
	private Button[] Btn = null;
	private Core core = new Core();
	private int xcount = 9, ycount = 9;
	private int longing=-1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		reatart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.FIRST + 1,1, "重新开始");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void setView(int x, int y) {
		// 获取屏幕大小，以合理设定 按钮 大小及位置
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		 //自定义layout组件
		RelativeLayout layout = new RelativeLayout(this);
		 //这里创建16个按钮，每行放置4个按钮
		Btn = new Button[x* y];
		int j = -1;
		for (int i = 0; i < x*y; i++) {
			Btn[i] = new Button(this);
			Btn[i].setId(2000 + i);
			Btn[i].setTextColor(Color.BLUE);
			RelativeLayout.LayoutParams btParams = new RelativeLayout.LayoutParams(
					(width - 50) /(x), (height-200)/(y));  //设置按钮的宽度和高度
			if (i % x == 0) {
				j++;
			}
			btParams.leftMargin = ((width - 50) / (x-1)) * (i % x);  //横坐标定位
			btParams.topMargin = ((height-200)/(y)+1)*j;  //纵坐标定位
			layout.addView(Btn[i], btParams); // 将按钮放入layout组件
		}
		this.setContentView(layout);
		 //批量设置监听
		for (int k = 0; k <Btn.length; k++) {
			 //这里不需要findId，因为创建的时候已经确定哪个按钮对应哪个Id
			Btn[k].setTag(k);  //为按钮设置一个标记，来确认是按下了哪一个按钮
			Btn[k].setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					int i = (Integer) v.getTag(); 
					if(longing==i){
						return;
					}
					Flag flag = core.Hit(i % ycount, i/ xcount);
					if(core.wasSolubed()){
						Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_LONG).show();
						repaint(true);
					}else if (flag.equals(Flag.Boom)) {
						Toast.makeText(MainActivity.this, "BOOM！", Toast.LENGTH_LONG).show();
						repaint(true);
					} else {
						repaint(false);
					}
				}
			});
			Btn[k].setLongClickable(true);
			Btn[k].setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					int i = (Integer) v.getTag();
					longing=i;
					core.setFlag(i % ycount, i/ xcount);
					if(core.sf[i/ xcount][i % ycount]){
						Btn[ i].setBackgroundResource(R.drawable.flag);
					}else{
						Btn[ i].setBackgroundColor(Color.DKGRAY);
					}
					return false;
				}
			});
		}
	}

	private void repaint(boolean showS) {
		for (int i = 0; i <core.data.length; i++) {
			for (int j = 0; j <core.data[i].length; j++) {
				if (showS) {
					Btn[i *xcount + j ].setEnabled(false);
					Btn[i *xcount + j ].setBackgroundColor(Color.WHITE);
					if(core.data[i][j].toInt()==-1){
						Btn[i *xcount + j ].setText("Boom");
					}else{
						Btn[i *xcount + j ].setText(core.data[i][j].toInt()+"");
					}
				} else {
					if(core.mask[i][j]){
						Btn[ i *xcount + j].setEnabled(false);
						Btn[ i *xcount + j].setBackgroundColor(Color.GRAY);
						Btn[ i *xcount + j].setText(core.data[i][j].toInt()==0?"":core.data[i][j].toInt()+"");
					}else{
						if(core.sf[i][j]){
							Btn[ i *xcount + j].setBackgroundResource(R.drawable.flag);//
						}else{
							Btn[ i *xcount + j].setBackgroundColor(Color.DKGRAY);
						}
					}
				}
			}
		}
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==Menu.FIRST + 1){
			reatart();
		}
        return true;
    }
	private void reatart() {
		setView(xcount, ycount);
		core = new Core();
		core.initial(xcount, ycount, xcount* ycount / 4, true);
		repaint(false);
	}
}
