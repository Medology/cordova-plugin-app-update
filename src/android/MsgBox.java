package com.vaenow.appupdate.android;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import org.apache.cordova.LOG;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LuoWen on 2016/1/20.
 */
public class MsgBox {
  public static final String TAG = "MsgBox";
  private Context mContext;
  private MsgHelper msgHelper;

  private Dialog noticeDialog;
  private AlertDialog downloadDialog;
  private ProgressBar downloadDialogProgress;
  private Dialog errorDialog;

  public MsgBox(Context mContext) {
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
      // Construction dialog
      final LayoutInflater inflater = LayoutInflater.from(mContext);
      View customTitle = inflater.inflate(msgHelper.getLayout("custom_title"), null);
      AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

      // TextView title = new TextView(this);
      // // You Can Customise your Title here 
      // title.setText(msgHelper.getString(MsgHelper.UPDATE_TITLE));
      // // title.setBackgroundColor(Color.DKGRAY);
      // title.setPadding(10, 10, 10, 10);
      // title.setGravity(Gravity.CENTER);
      // title.setTextColor(Color.WHITE);
      // title.setTextSize(20);

      builder.setCustomTitle(customTitle);

      // builder.setTitle(msgHelper.getString(MsgHelper.UPDATE_TITLE));
      builder.setMessage(msgHelper.getString(MsgHelper.UPDATE_MESSAGE));
      // Update
      builder.setPositiveButton(msgHelper.getString(MsgHelper.UPDATE_UPDATE_BTN), onClickListener);
      noticeDialog = builder.create();
    }

    if (!noticeDialog.isShowing()) noticeDialog.show();

    noticeDialog.setCanceledOnTouchOutside(false);// Set the click screen Dialog does not disappear
    return noticeDialog;
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
