package com.cynerds.cyburger.activities;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.admin.ManageCombosActivity;
import com.cynerds.cyburger.activities.admin.ManageItemsActivity;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.components.Badge;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.GsonHelper;
import com.cynerds.cyburger.models.combos.Combo;
import com.cynerds.cyburger.models.customer.Customer;
import com.cynerds.cyburger.models.items.Item;
import com.cynerds.cyburger.models.orders.Order;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by fabri on 08/07/2017.
 */

public class BaseActivity extends AppCompatActivity {

    private FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelperOrders;
    private ArrayList dirty = new ArrayList<>();
    private Runnable onPermissionGrantedAction;
    private Runnable onPermissionDeniedAction;
    private boolean ignoreUnsavedChanges;
    private String unsavedChangesMessage;

    private Bundle savedInstanceState;
    private View.OnClickListener onSaveListener;
    private View.OnClickListener onCancelListener;
    private TextView actionBarTitle;
    private Badge badge;
    private View hamburgerMenu;
    private Order order;

    public BaseActivity() {

        firebaseRealtimeDatabaseHelperOrders = new FirebaseRealtimeDatabaseHelper(Order.class);
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Badge getBadge() {
        return badge;
    }

    public View getView() {


        return findViewById(android.R.id.content);
    }

    public View.OnClickListener getOnSaveListener() {
        return onSaveListener;
    }

    public void setOnSaveListener(View.OnClickListener onSaveListener) {
        this.onSaveListener = onSaveListener;
    }

    public View.OnClickListener getOnCancelListener() {
        return onCancelListener;
    }

    public void setOnCancelListener(View.OnClickListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    protected void setOnPermissionGrantedAction(Runnable onPermissionGrantedAction) {
        this.onPermissionGrantedAction = onPermissionGrantedAction;
    }

    protected void setOnPermissionDeniedAction(Runnable onPermissionDeniedAction) {
        this.onPermissionDeniedAction = onPermissionDeniedAction;
    }

    protected boolean isIgnoreUnsavedChanges() {
        return ignoreUnsavedChanges;
    }

    protected void setIgnoreUnsavedChanges(boolean ignoreUnsavedChanges) {
        this.ignoreUnsavedChanges = ignoreUnsavedChanges;
    }

    protected String getUnsavedChangesMessage() {
        return unsavedChangesMessage;
    }

    protected void setUnsavedChangesMessage(String unsavedChangesMessage) {
        this.unsavedChangesMessage = unsavedChangesMessage;
    }

    @Override
    public void onBackPressed() {

        closeActivity();

    }

    protected boolean isWorkspaceDirty() {

        return dirty.size() > 0;
    }

    protected void setActionBarTitle(String title) {
        actionBarTitle.setText(title);

    }

    protected void closeActivity() {

        if (isWorkspaceDirty() && !ignoreUnsavedChanges) {

            DialogAction dialogAction = new DialogAction();
            final DialogManager dialogManager = new DialogManager(this,
                    DialogManager.DialogType.YES_NO);
            dialogManager.setAction(dialogAction);

            dialogAction.setPositiveAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            dialogAction.setNegativeAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogManager.closeDialog();
                }
            });

            dialogManager.showDialog("Você possui alterações. Deseja sair?");

        } else {
            finish();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.savedInstanceState = savedInstanceState;

        setUIEvents();


    }

    private void setUIEvents() {

        order = new Order();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //bellow setSupportActionBar(toolbar);
        actionBar.setCustomView(R.layout.base_titlebar);

        actionBarTitle = findViewById(R.id.action_bar_title);


        getView().setFocusableInTouchMode(true);

        badge = findViewById(R.id.badge);
        hamburgerMenu = findViewById(R.id.hamburgerMenu);

        hamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(BaseActivity.this, hamburgerMenu);
                popupMenu.inflate(R.menu.menu_overflow);

                popupMenu.getMenu().findItem(R.id.action_profile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ActivityManager.startActivity(BaseActivity.this, ProfileActivity.class);
                        return false;
                    }
                });


                popupMenu.getMenu().findItem(R.id.action_manage_items).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ActivityManager.startActivity(BaseActivity.this, ManageItemsActivity.class);
                        return false;
                    }
                });

                popupMenu.getMenu().findItem(R.id.action_manage_combos).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ActivityManager.startActivity(BaseActivity.this, ManageCombosActivity.class);
                        return false;
                    }
                });

                popupMenu.show();

            }
        });


        badge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                displayOrderDialog();


                return false;
            }
        });

    }

    public void displayOrderDialog() {

        final boolean isPreview = order.getKey() != null;

        final DialogManager dialogManager = new DialogManager(BaseActivity.this);
        dialogManager.setContentView(R.layout.dialog_ordering_items);
        dialogManager.showDialog("Seu pedido", "");


        TextView orderedItemsTxtView = dialogManager.getContentView().findViewById(R.id.orderedItemsTxtView);
        TextView orderedItemsAmountTxtView = dialogManager.getContentView().findViewById(R.id.orderedItemsAmountTxtView);

        String orderedItemsString = "";
        String orderedItemsAmountString = "";
        float orderedItemsAmount = 0;

        for (Combo combo :
                order.getOrderedCombos()) {

            orderedItemsAmount += combo.getComboAmount();
            orderedItemsString += combo.getComboName() + " - R$ " + combo.getComboAmount() + "\n";
        }

        for (Item item :
                order.getOrderedItems()) {

            orderedItemsAmount += item.getPrice();
            orderedItemsString += item.getDescription() + " - R$ " + item.getPrice() + "\n";
        }


        orderedItemsAmountString = "R$ " + String.valueOf(orderedItemsAmount);
        orderedItemsAmountTxtView.setText(orderedItemsAmountString);

        if (!orderedItemsString.isEmpty()) {
            orderedItemsTxtView.setText(orderedItemsString);
        }

        if (order.getOrderedItems().size() > 0 || order.getOrderedCombos().size() > 0 || isPreview) {

            Button confirmOrderBtn = dialogManager.getContentView().findViewById(R.id.confirmOrderBtn);
            Button removeOrderBtn = dialogManager.getContentView().findViewById(R.id.removeOrderBtn);


            if (!isPreview) {
                confirmOrderBtn.setVisibility(View.VISIBLE);
                confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String customerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        Customer customer = new Customer();
                        customer.setCustomerName(customerName);
                        customer.setLinkedProfileId(CyburgerApplication.getProfile().getUserId());

                        order.setCustomer(customer);
                        firebaseRealtimeDatabaseHelperOrders.insert(order);

                        Toast.makeText(BaseActivity.this, "Pedido confirmado", Toast.LENGTH_SHORT).show();

                        //Reset - pedido confirmado
                        badge.setBadgeCount(0);
                        order = new Order();
                        dialogManager.closeDialog();
                    }
                });
            } else {
                dialogManager.setOnCanceListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        order = new Order();
                        Toast.makeText(BaseActivity.this, "reset order", Toast.LENGTH_SHORT).show();
                    }
                });

            }


            removeOrderBtn.setVisibility(View.VISIBLE);
            removeOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(BaseActivity.this, "Pedido cancelado", Toast.LENGTH_SHORT).show();

                    if (!isPreview) {
                        firebaseRealtimeDatabaseHelperOrders.delete(order);
                    }

                    //Reset - pedido cancelado
                    badge.setBadgeCount(0);
                    order = new Order();
                    dialogManager.closeDialog();

                }
            });


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (onPermissionGrantedAction != null) {

                        onPermissionGrantedAction.run();
                    }
                } else {


                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public Object getExtra(Class type) {
        Object newObject;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                newObject = null;
            } else {
                newObject = extras.getString(type.getSimpleName());
            }
        } else {
            newObject = savedInstanceState.getSerializable(type.getSimpleName());
        }

        newObject = GsonHelper.ToObject(type, (String) newObject);
        return newObject;
    }

    protected void showBadge(boolean showBadge) {

        if (showBadge) {
            badge.setVisibility(View.VISIBLE);

        } else {
            badge.setVisibility(View.INVISIBLE);

        }
    }

    protected void showActionBarMenu(boolean showMenu) {

        if (showMenu) {


            if (showMenu) {
                hamburgerMenu.setVisibility(View.VISIBLE);

            } else {
                hamburgerMenu.setVisibility(View.INVISIBLE);

            }
        }
    }
}

