package com.baskom.masakbanyak.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baskom.masakbanyak.R;
import com.baskom.masakbanyak.di.Components;
import com.baskom.masakbanyak.model.Order;
import com.baskom.masakbanyak.model.Packet;
import com.baskom.masakbanyak.viewmodel.CateringViewModel;
import com.baskom.masakbanyak.viewmodel.OrderViewModel;
import com.baskom.masakbanyak.viewmodel.ViewModelFactory;

import java.text.NumberFormat;
import java.util.Locale;

import javax.inject.Inject;

public class TransactionActivity extends AppCompatActivity {
  
  @Inject
  ViewModelFactory mViewModelFactory;
  
  private OrderViewModel mOrderViewModel;
  private CateringViewModel mCateringViewModel;
  
  private Order mOrder;
  private Packet mPacket;
  
  private CoordinatorLayout mCoordinatorLayout;
  private SwipeRefreshLayout mRefreshLayout;
  private TextView mStatus;
  private TextView mBank;
  private TextView mVirtualAccount;
  private TextView mTotalPrice;
  private TextView mPacketName;
  private TextView mQuantity;
  private TextView mTime;
  private TextView mAddress;
  private LinearLayout mContents;
  private Button mRefundButton;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_transaction);
  
    Components.getSessionComponent().inject(this);
    
    mOrder = (Order) getIntent().getSerializableExtra("order");
  
    mOrderViewModel = ViewModelProviders.of(this, mViewModelFactory).get(OrderViewModel.class);
    mCateringViewModel = ViewModelProviders.of(this, mViewModelFactory).get(CateringViewModel.class);
    
    mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
    mRefreshLayout = findViewById(R.id.refreshLayout);
    mStatus = findViewById(R.id.transactionStatusTextView);
    mBank = findViewById(R.id.bankTextView);
    mVirtualAccount = findViewById(R.id.virtualAccountNumberTextView);
    mTotalPrice = findViewById(R.id.totalPriceTextView);
    mPacketName = findViewById(R.id.packetNameTextView);
    mQuantity = findViewById(R.id.quantityTextView);
    mTime = findViewById(R.id.timeTextView);
    mAddress = findViewById(R.id.addressTextView);
    mContents = findViewById(R.id.packetContentsLayout);
    mRefundButton = findViewById(R.id.refundButton);
  
    mRefundButton.setVisibility(View.INVISIBLE);
    
    mRefreshLayout.setRefreshing(true);
    mRefreshLayout.setOnRefreshListener(() -> mCateringViewModel.refreshPacketByOrder(mOrder));
    
    mCateringViewModel.getPacketLiveDataByOrder(mOrder).observe(this, packet -> {
      this.mPacket = packet;
      
      mStatus.setText(mOrder.getStatus());
      mBank.setText(mOrder.getVirtual_account().getBank().toUpperCase());
      mVirtualAccount.setText(mOrder.getVirtual_account().getNumber());
      mTotalPrice.setText("Rp " + NumberFormat.getNumberInstance(Locale.US).format(mOrder.getTotal_price()));
      mPacketName.setText(mPacket.getName());
      mQuantity.setText(Integer.toString(mOrder.getQuantity()));
      mTime.setText(mOrder.getEvent_time());
      mAddress.setText(mOrder.getEvent_address());
      
      mContents.removeAllViews();
      for (int i = 0; i < mPacket.getContents().size(); i++) {
        TextView content = (TextView) getLayoutInflater().inflate(R.layout.itemview_packet_content_alternative, null);
        content.setText(mPacket.getContents().get(i));
        mContents.addView(content);
      }
  
      if(mOrder.getStatus().toLowerCase().equals("settlement")){
        mRefundButton.setOnClickListener(view -> mOrderViewModel.refundOrder(mOrder));
        mRefundButton.setVisibility(View.VISIBLE);
      }
      
      mRefreshLayout.setRefreshing(false);
    });
  
    mOrderViewModel.getNotificationEventLiveData().observe(this, notificationEvent -> {
      String notification = notificationEvent.getContentIfNotHandled();
    
      if(notification != null){
        showResponse(notification);
      }
    });
  }
  
  public void showResponse(String response) {
    Snackbar.make(mCoordinatorLayout, response, Snackbar.LENGTH_SHORT).show();
  }
  
}
