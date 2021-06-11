package com.vaenow.appupdate.android;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.app.DialogFragment;
import android.util.Log;
import android.graphics.Color;
import android.widget.Button;
import android.graphics.drawable.GradientDrawable;
import org.apache.cordova.LOG;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;
import org.json.JSONException;



import java.util.HashMap;
import java.util.Map;

/**
 * Created by LuoWen on 2016/1/20.
 */
public class MsgBox {
  public static final String TAG = "MsgBox";
  private Context mContext;
  private CallbackContext callback;
  private MsgHelper msgHelper;

  private AlertDialog noticeDialog;
  private AlertDialog downloadDialog;
  private ProgressBar downloadDialogProgress;
  private Dialog errorDialog;

  public MsgBox(Context mContext, CallbackContext callback) {
    Log.d("msgbox", "msgbox with callback");
    this.mContext = mContext;
    this.callback = callback;
    this.msgHelper = new MsgHelper(mContext.getPackageName(), mContext.getResources());
  }
  
  public MsgBox(Context mContext) {
    Log.d("msgbox", "msgbox only");
    this.mContext = mContext;
    this.msgHelper = new MsgHelper(mContext.getPackageName(), mContext.getResources());
  }

  /**
   * Display software update dialog
   *
   * @param onClickListener
   */
  public Dialog showNoticeDialog(OnClickListener onClickListener) {
    if (noticeDialog == null) {
      LOG.d(TAG, "showNoticeDialog");
      AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
      builder.setTitle(msgHelper.getString(MsgHelper.UPDATE_TITLE));
      builder.setPositiveButton("Debug", new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          try {
            JSONObject response = new JSONObject();
            response.put("debugClicked", true);
            PluginResult result = new PluginResult(PluginResult.Status.OK, response);
            result.setKeepCallback(true);
            callback.sendPluginResult(result); // this keeps prevents cbContext from closing */
            ((AlertDialog)noticeDialog).getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.VISIBLE); // if the user taps 'check updates' again, this show the icon button again 
          } catch (JSONException e) {
            Log.d(TAG, "Error occurred sending transfering state " + e.getMessage());
          }
        }});
      builder.setMessage(msgHelper.getString(MsgHelper.UPDATE_MESSAGE));
      builder.setNeutralButton(msgHelper.getString(MsgHelper.UPDATE_UPDATE_BTN), onClickListener);
      builder.setNegativeButton("...", new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            //Do nothing here because we override this button later to change the close behaviour. 
            //However, we still need this because on older versions of Android unless we 
            //pass a handler the button doesn't get instantiated
        }       
        });
      noticeDialog = builder.create();
      }
      
    if (!noticeDialog.isShowing()){
        noticeDialog.show();
        // debug button styles
        Button debugButton = ((AlertDialog)noticeDialog).getButton(AlertDialog.BUTTON_POSITIVE);
        debugButton.setVisibility(View.GONE);
        GradientDrawable border = new GradientDrawable();
        border.setColor(Color.TRANSPARENT); 
        border.setStroke(1, 0xFF000000); 
        debugButton.setBackground(border);
        debugButton.setPadding(0,11,0,11);
        // icon button styles
        Button iconButton = ((AlertDialog)noticeDialog).getButton(AlertDialog.BUTTON_NEGATIVE);
        iconButton.setVisibility(View.VISIBLE);
        iconButton.setRotation(90);
        iconButton.setTextSize(30);
        iconButton.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout.LayoutParams iconButtonParams = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.WRAP_CONTENT,
          LinearLayout.LayoutParams.WRAP_CONTENT
          );
        iconButtonParams.setMargins(16,-7,0,0);
        iconButton.setLayoutParams(iconButtonParams);
        iconButton.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v) {
            ((AlertDialog)noticeDialog).getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
            ((AlertDialog)noticeDialog).getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
          }
        });
      } 

    noticeDialog.setCanceledOnTouchOutside(false);// Set the click screen Dialog does not disappear
    return noticeDialog;
  }

  public void unhideUpdateModal() {
    noticeDialog.show();
  }

  /**
   * Display software download dialog
   */
  public Map<String, Object> showDownloadDialog(
    OnClickListener onClickListenerNeg,
    OnClickListener onClickListenerPos,
    OnClickListener onClickListenerNeu,
    boolean showDialog
  ) {
    if (downloadDialog == null) {
      LOG.d(TAG, "showDownloadDialog");

      // Construct software download dialog
      AlertDialog.Builder builder = new Builder(mContext);
      builder.setTitle(msgHelper.getString(MsgHelper.UPDATING));
      // Add a progress bar to the download dialog
      final LayoutInflater inflater = LayoutInflater.from(mContext);
      View v = inflater.inflate(msgHelper.getLayout(MsgHelper.APPUPDATE_PROGRESS), null);

      /* Update progress bar */
      downloadDialogProgress = (ProgressBar) v.findViewById(msgHelper.getId(MsgHelper.UPDATE_PROGRESS));
      builder.setView(v);
      builder.setNeutralButton(msgHelper.getString(MsgHelper.DOWNLOAD_COMPLETE_NEU_BTN), onClickListenerNeu);
      builder.setPositiveButton(msgHelper.getString(MsgHelper.DOWNLOAD_COMPLETE_POS_BTN), onClickListenerPos);
      downloadDialog = builder.create();
    }

    if (showDialog && !downloadDialog.isShowing())
      downloadDialog.show();

    downloadDialog.setTitle(msgHelper.getString(MsgHelper.UPDATING));
    downloadDialog.setCanceledOnTouchOutside(false);// Set the click screen Dialog does not disappear
    if (downloadDialog.isShowing()) {
      downloadDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(View.GONE); // Install Manually
      downloadDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE); // Download Again
    }

    Map<String, Object> ret = new HashMap<String, Object>();
    ret.put("dialog", downloadDialog);
    ret.put("progress", downloadDialogProgress);
    return ret;
  }

  /**
   * Error prompt window
   *
   * @param errorDialogOnClick
   */
  public Dialog showErrorDialog(OnClickListener errorDialogOnClick) {
    if (this.errorDialog == null) {
      LOG.d(TAG, "initErrorDialog");
      // Construction dialog
      AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
      builder.setTitle(msgHelper.getString(MsgHelper.UPDATE_ERROR_TITLE));
      builder.setMessage(msgHelper.getString(MsgHelper.UPDATE_ERROR_MESSAGE));
      // Update
      builder.setPositiveButton(msgHelper.getString(MsgHelper.UPDATE_ERROR_YES_BTN), errorDialogOnClick);
      errorDialog = builder.create();
    }

    if (!errorDialog.isShowing())
      errorDialog.show();

    return errorDialog;
  }

}
