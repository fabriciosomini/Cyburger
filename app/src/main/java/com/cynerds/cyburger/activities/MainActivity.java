package com.cynerds.cyburger.activities;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.admin.ManageCombosActivity;
import com.cynerds.cyburger.activities.admin.ManageItemsActivity;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.components.Badge;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.fragments.CombosFragment;
import com.cynerds.cyburger.fragments.ItemsMenuFragment;
import com.cynerds.cyburger.fragments.OrdersFragment;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.helpers.MessageHelper;
import com.cynerds.cyburger.models.combos.Combo;
import com.cynerds.cyburger.models.customer.Customer;
import com.cynerds.cyburger.models.general.MessageType;
import com.cynerds.cyburger.models.items.Item;
import com.cynerds.cyburger.models.orders.Order;
import com.cynerds.cyburger.models.profile.Profile;
import com.google.firebase.auth.FirebaseAuth;



public class MainActivity extends BaseActivity {

    public static final int COMBO_TAB = 0;
    public static final int ITEMS_TAB = 1;
    public static final int ORDERS_TAB = 2;
    FragmentManager fragmentManager = getSupportFragmentManager();
    CombosFragment combosFragment = new CombosFragment();
    ItemsMenuFragment itemsMenuFragment = new ItemsMenuFragment();
    OrdersFragment ordersFragment = new OrdersFragment();
    private FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelperOrders;
    private FirebaseRealtimeDatabaseHelper<Profile> firebaseRealtimeDatabaseHelperProfile;
    private Badge badge;
    private View hamburgerMenu;
    private ImageButton hamburgerMenuIcon;
    private Order order;
    private Order previousOrder;
    private AHBottomNavigation bottomNavigation;
    private int[] notifications = new int[3];
    private AHBottomNavigation.OnTabSelectedListener mOnNavigationItemSelectedListener = new AHBottomNavigation.OnTabSelectedListener() {
        @Override
        public boolean onTabSelected(int position, boolean wasSelected) {
            switch (position) {

                case COMBO_TAB:

                    fragmentManager.beginTransaction().replace(R.id.contentLayout,
                            combosFragment,
                            combosFragment.getTag()).commit();
                    setActionBarTitle(getString(R.string.title_combos));

                    return true;

                case ITEMS_TAB:
                    fragmentManager.beginTransaction().replace(R.id.contentLayout,
                            itemsMenuFragment,
                            itemsMenuFragment.getTag()).commit();
                    setActionBarTitle(getString(R.string.title_dashboard));

                    return true;

                case ORDERS_TAB:
                    fragmentManager.beginTransaction().replace(R.id.contentLayout,
                            ordersFragment,
                            ordersFragment.getTag()).commit();
                    setActionBarTitle(getString(R.string.title_notifications));


            }
            return true;
        }
    };


    public MainActivity() {
        firebaseRealtimeDatabaseHelperOrders = new FirebaseRealtimeDatabaseHelper(Order.class);
        firebaseRealtimeDatabaseHelperProfile = new FirebaseRealtimeDatabaseHelper(Profile.class);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setUIEvents();

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {

       /* bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.redishBrown));
        bottomNavigation.getBackground().setAlpha(25);*/
        return super.onCreateView(name, context, attrs);
    }

    private void setUIEvents() {

        order = new Order();
        badge = findViewById(R.id.badge);
        hamburgerMenu = findViewById(R.id.hamburgerMenu);
        hamburgerMenuIcon = findViewById(R.id.hamburgerMenuIcon);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.navigation);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation);
        bottomNavigation.setOnTabSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigation.setCurrentItem(COMBO_TAB);
        bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.redishOrange0));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.mediumgrey));
        fragmentManager.beginTransaction().attach(ordersFragment).commit();


        setActionBarTitle(getString(R.string.title_combos));
        showActionBarMenu(true);


        final PopupMenu popupMenu = new PopupMenu(MainActivity.this, hamburgerMenu);
        popupMenu.inflate(R.menu.menu_overflow);


        View.OnTouchListener hamburgerMenuTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                popupMenu.getMenu().findItem(R.id.action_profile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ActivityManager.startActivity(MainActivity.this, ProfileActivity.class);
                        return false;
                    }
                });


                if (CyburgerApplication.isAdmin()) {
                    popupMenu.getMenu().findItem(R.id.action_manage_items).setVisible(true);
                    popupMenu.getMenu().findItem(R.id.action_manage_combos).setVisible(true);

                    popupMenu.getMenu().findItem(R.id.action_manage_items).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            ActivityManager.startActivity(MainActivity.this, ManageItemsActivity.class);
                            return false;
                        }
                    });

                    popupMenu.getMenu().findItem(R.id.action_manage_combos).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            ActivityManager.startActivity(MainActivity.this, ManageCombosActivity.class);
                            return false;
                        }
                    });
                }
                else{
                    popupMenu.getMenu().findItem(R.id.action_manage_items).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.action_manage_combos).setVisible(false);
                }

                popupMenu.show();

                return false;
            }
        };

        hamburgerMenuIcon.setOnTouchListener(hamburgerMenuTouchListener);
        hamburgerMenu.setOnTouchListener(hamburgerMenuTouchListener);


        badge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                displayOrderDialog();


                return false;
            }
        });
    }

    public void displayOrderDialog() {

        final boolean readOnly = order.getKey() != null;

        String title = "Fazer pedido";
        if (readOnly) {
            title = "PEDIDO CONFIRMADO";
        }

        final DialogManager orderDialog = new DialogManager(MainActivity.this);
        orderDialog.setContentView(R.layout.dialog_ordering_items);
        orderDialog.showDialog(title, "");


        TextView orderedItemsTxtView = orderDialog.getContentView().findViewById(R.id.orderedItemsTxtView);
        TextView orderedItemsAmountTxtView = orderDialog.getContentView().findViewById(R.id.orderedItemsAmountTxtView);

        String orderedItemsString = "";
        String orderedItemsAmountString = "";
        float orderedItemsAmount = 0;

        for (Combo combo :
                order.getOrderedCombos()) {

            orderedItemsAmount += combo.getComboAmount();
            orderedItemsString += combo.getComboName() + ": R$ " + combo.getComboAmount() + " (" + combo.getComboBonusPoints() + " pontos)" + "\n";
        }

        for (Item item :
                order.getOrderedItems()) {

            orderedItemsAmount += item.getPrice();
            orderedItemsString += item.getDescription() + ": R$ " + item.getPrice() + " (" + item.getBonusPoints() + " pontos)" + "\n";
        }


        orderedItemsAmountString = "R$ " + String.format("%.2f", orderedItemsAmount);
        orderedItemsAmountTxtView.setText(orderedItemsAmountString);

        if (!orderedItemsString.isEmpty()) {
            orderedItemsTxtView.setText(orderedItemsString);
        }

        if (order.getOrderedItems().size() > 0 || order.getOrderedCombos().size() > 0 || readOnly) {

            Button confirmOrderBtn = orderDialog.getContentView().findViewById(R.id.confirmOrderBtn);
            Button removeOrderBtn = orderDialog.getContentView().findViewById(R.id.removeOrderBtn);


            if (readOnly) {



                if(CyburgerApplication.isAdmin())
                {
                    confirmOrderBtn.setText(getString(R.string.order_finishOrder));
                    confirmOrderBtn.setVisibility(View.VISIBLE);

                    confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                           final DialogManager confirmFinishOrderDialog = new DialogManager(MainActivity.this,
                                    DialogManager.DialogType.YES_NO);

                            DialogAction confirmFinishOrderDialogAction = new DialogAction();
                            confirmFinishOrderDialogAction.setPositiveAction(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    for (Profile p :
                                            firebaseRealtimeDatabaseHelperProfile.get()) {
                                       if(order.getCustomer().getLinkedProfileId().equals(p.getUserId()))
                                       {
                                           int comboBonusPoints = 0;
                                           int itemsBonusPoints = 0;


                                           for (Combo combo :
                                                   order.getOrderedCombos()) {
                                               comboBonusPoints += combo.getComboBonusPoints();
                                           }

                                           for (Item item:
                                                   order.getOrderedItems()) {
                                               itemsBonusPoints += item.getBonusPoints();
                                           }

                                           int totalBonusPoints = comboBonusPoints
                                                   + itemsBonusPoints
                                                   + p.getBonusPoints();

                                           p.setBonusPoints(totalBonusPoints);
                                           firebaseRealtimeDatabaseHelperProfile.update(p);
                                           firebaseRealtimeDatabaseHelperOrders.delete(order);
                                           confirmFinishOrderDialog.closeDialog();
                                           orderDialog.closeDialog();
                                           MessageHelper.show(MainActivity.this,
                                                   MessageType.SUCCESS, "Pedido concluído!");
                                           return;
                                       }
                                    }

                                    MessageHelper.show(MainActivity.this,
                                            MessageType.ERROR,
                                            "Erro ao vincular os pontos ao perfil");
                                }

                            });


                            confirmFinishOrderDialog.setAction(confirmFinishOrderDialogAction);
                            confirmFinishOrderDialog.showDialog("CONCLUIR PEDIDO");
                        }
                    });

                }else{


                    removeOrderBtn.getLayoutParams().width = (int)getResources().getDimension(R.dimen.default_width);
                }



                orderDialog.setOnCanceListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (previousOrder != null) {
                            order = previousOrder;
                            LogHelper.show("Restore previous order");
                        } else {
                            order = new Order();
                            LogHelper.show("reset order");
                        }


                    }
                });

            } else {

                previousOrder = order;
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

                        LogHelper.show("Pedido confirmado");

                        //Reset - pedido confirmado
                        badge.setBadgeCount(0);
                        order = new Order();
                        previousOrder = new Order();
                        orderDialog.closeDialog();


                        MessageHelper.show(MainActivity.this,
                                MessageType.SUCCESS,
                                "Tudo certo! Seu pedido vai chegar logo");


                    }
                });
            }


            removeOrderBtn.setVisibility(View.VISIBLE);
            removeOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LogHelper.show("Pedido cancelado");

                    DialogAction removeOrderDialogAction = new DialogAction();
                    removeOrderDialogAction.setPositiveAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (readOnly) {
                                firebaseRealtimeDatabaseHelperOrders.delete(order);
                                removeNotification(ORDERS_TAB, 1);

                            } else {
                                previousOrder = new Order();
                                badge.setBadgeCount(0);
                            }

                            if (previousOrder != null) {
                                order = previousOrder;
                                LogHelper.show("Restore previous order");
                            } else {
                                order = new Order();
                                LogHelper.show("reset order");
                            }


                            orderDialog.closeDialog();
                        }
                    });

                    DialogManager removeOrderDialog = new DialogManager(MainActivity.this,
                            DialogManager.DialogType.YES_NO);
                    removeOrderDialog.setAction(removeOrderDialogAction);
                    removeOrderDialog.showDialog("Cancelar Pedido","Deseja cancelar o pedido?");



                }
            });


        }

    }

    private void showActionBarMenu(boolean showMenu) {

        if (showMenu) {


            if (showMenu) {
                hamburgerMenu.setVisibility(View.VISIBLE);

            } else {
                hamburgerMenu.setVisibility(View.INVISIBLE);

            }
        }
    }

    @Override
    public void onBackPressed() {

        final DialogManager dialogManager = new DialogManager(this);
        dialogManager.setContentView(R.layout.dialog_confirm_exit);
        dialogManager.showDialog("Sair do aplicativo", "");

        dialogManager.getContentView().findViewById(R.id.confirmExitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialogManager.getContentView().findViewById(R.id.cancelExitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogManager.closeDialog();
            }
        });

    }

    public void addNotification(int tabIndex, int notificationCount) {
        notifications[tabIndex] += notificationCount;

        setNotification(tabIndex, notifications[tabIndex]);
    }

    public void removeNotification(int tabIndex, int notificationCount) {

        notifications[tabIndex] -= notificationCount;
        setNotification(tabIndex, notifications[tabIndex]);
    }

    private void setNotification(int tabIndex, int count) {


        AHNotification notification = new AHNotification.Builder()
                .setText(String.valueOf(count == 0 ? "" : count))
                .setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setTextColor(ContextCompat.getColor(this, R.color.verylightgrey))
                .build();
        bottomNavigation.setNotification(notification, tabIndex);
    }


    public void clearNotifications(int tabIndex) {

        notifications[tabIndex] = 0;
        setNotification(tabIndex, notifications[tabIndex]);
    }
}
