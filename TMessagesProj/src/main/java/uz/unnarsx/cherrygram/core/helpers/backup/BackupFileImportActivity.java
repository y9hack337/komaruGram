package uz.unnarsx.cherrygram.core.helpers.backup;

import static org.telegram.messenger.LocaleController.getString;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.text.InputFilter;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.PhotoPickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import uz.unnarsx.cherrygram.core.configs.CherrygramChatsConfig;

public class BackupFileImportActivity extends BaseFragment {

    public interface DocumentSelectActivityDelegate {
        void didSelectFiles(BackupFileImportActivity activity, ArrayList<String> files, String caption, boolean notify, int scheduleDate);

        void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate);

        void startDocumentSelectActivity();

        default void startMusicSelectActivity() {
        }
    }

    private RecyclerListView listView;
    private ListAdapter listAdapter;
    private ActionBarMenuItem sortItem;
    private LinearLayoutManager layoutManager;
    private ChatActivity chatActivity;

    private LinearLayout emptyView;
    private ImageView emptyImageView;
    private TextView emptyTitleTextView;
    private TextView emptySubtitleTextView;

    private FrameLayout frameLayout2;
    private FrameLayout writeButtonContainer;
    private View selectedCountView;
    private View shadow;
    private EditTextEmoji commentTextView;
    private ImageView writeButton;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private AnimatorSet animatorSet;

    private boolean sendPressed;

    private ActionBarPopupWindow sendPopupWindow;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarMenuSubItem[] itemCells;

    private boolean hasFiles;

    private File currentDir;
    private boolean receiverRegistered = false;
    private static final long sizeLimit = 1024 * 1024 * 1536;
    private DocumentSelectActivityDelegate delegate;
    private HashMap<String, ListItem> selectedFiles = new HashMap<>();
    private boolean scrolling;
    private int maxSelectedFiles = -1;

    private boolean sortByName;

    private final static int sort_button = 0;

    private static class ListItem {
        public int icon;
        public String title;
        public String subtitle = "";
        public String ext = "";
        public String thumb;
        public File file;
    }

    private static class HistoryEntry {
        int scrollItem, scrollOffset;
        File dir;
        String title;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            Runnable r = () -> {
                try {
                    if (currentDir == null) {
                        listRoots();
                    } else {
                        listFiles(currentDir);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            };
            if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
                listView.postDelayed(r, 1000);
            } else {
                r.run();
            }
        }
    };

    public BackupFileImportActivity() {
        super();
    }

    @Override
    public boolean onFragmentCreate() {
        sortByName = SharedConfig.sortFilesByName;
        loadRecentFiles();
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        if (commentTextView != null) {
            commentTextView.onDestroy();
        }
        try {
            if (receiverRegistered) {
                ApplicationLoader.applicationContext.unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        super.onFragmentDestroy();
    }

    @Override
    public View createView(Context context) {
        if (!receiverRegistered) {
            receiverRegistered = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
            filter.addAction(Intent.ACTION_MEDIA_CHECKING);
            filter.addAction(Intent.ACTION_MEDIA_EJECT);
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter.addAction(Intent.ACTION_MEDIA_NOFS);
            filter.addAction(Intent.ACTION_MEDIA_REMOVED);
            filter.addAction(Intent.ACTION_MEDIA_SHARED);
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            filter.addDataScheme("file");
            if (Build.VERSION.SDK_INT >= 33) {
                ApplicationLoader.applicationContext.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                ApplicationLoader.applicationContext.registerReceiver(receiver, filter);
            }
        }

        actionBar.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        actionBar.setTitleColor(Theme.getColor(Theme.key_dialogTextBlack));
        actionBar.setItemsColor(Theme.getColor(Theme.key_dialogTextBlack), false);
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_dialogButtonSelector), false);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(getString(R.string.SelectFile));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    if (canClosePicker()) {
                        finishFragment();
                    }
                } else if (id == sort_button) {
                    SharedConfig.toggleSortFilesByName();
                    sortByName = SharedConfig.sortFilesByName;
                    sortRecentItems();
                    sortFileItems();
                    listAdapter.notifyDataSetChanged();
                    sortItem.setIcon(sortByName ? R.drawable.msg_contacts_time : R.drawable.msg_contacts_name);
                }
            }
        });

        final ActionBarMenu menu = actionBar.createMenu();

        sortItem = menu.addItem(sort_button, sortByName ? R.drawable.msg_contacts_time : R.drawable.msg_contacts_name);
        sortItem.setContentDescription(getString(R.string.AccDescrContactSorting));

        selectedFiles.clear();

        sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context,  parentLayout) {

            private int lastNotifyWidth;
            private boolean ignoreLayout;

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);

                setMeasuredDimension(widthSize, heightSize);

                int kbHeight = getKeyboardHeight();
                if (kbHeight <= AndroidUtilities.dp(20)) {
                    if (!AndroidUtilities.isInMultiwindow && commentTextView != null && frameLayout2.getParent() == this) {
                        heightSize -= commentTextView.getEmojiPadding();
                        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
                    }
                }

                if (kbHeight > AndroidUtilities.dp(20) && commentTextView != null) {
                    ignoreLayout = true;
                    commentTextView.hideEmojiView();
                    ignoreLayout = false;
                }

                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (child == null || child.getVisibility() == GONE) {
                        continue;
                    }
                    if (commentTextView != null && commentTextView.isPopupView(child)) {
                        if (AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) {
                            if (AndroidUtilities.isTablet()) {
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200 : 320), heightSize - AndroidUtilities.statusBarHeight + getPaddingTop()), MeasureSpec.EXACTLY));
                            } else {
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize - AndroidUtilities.statusBarHeight + getPaddingTop(), MeasureSpec.EXACTLY));
                            }
                        } else {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, MeasureSpec.EXACTLY));
                        }
                    } else {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    }
                }
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                if (lastNotifyWidth != r - l) {
                    lastNotifyWidth = r - l;
                    if (sendPopupWindow != null && sendPopupWindow.isShowing()) {
                        sendPopupWindow.dismiss();
                    }
                }
                final int count = getChildCount();

                int keyboardSize = getKeyboardHeight();
                int paddingBottom = commentTextView != null && frameLayout2.getParent() == this && keyboardSize <= AndroidUtilities.dp(20) && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet() ? commentTextView.getEmojiPadding() : 0;
                setBottomClip(paddingBottom);

                for (int i = 0; i < count; i++) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() == GONE) {
                        continue;
                    }
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                    final int width = child.getMeasuredWidth();
                    final int height = child.getMeasuredHeight();

                    int childLeft;
                    int childTop;

                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = Gravity.TOP | Gravity.LEFT;
                    }

                    final int absoluteGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
                    final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

                    switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                        case Gravity.CENTER_HORIZONTAL:
                            childLeft = (r - l - width) / 2 + lp.leftMargin - lp.rightMargin;
                            break;
                        case Gravity.RIGHT:
                            childLeft = (r - l) - width - lp.rightMargin - getPaddingRight();
                            break;
                        case Gravity.LEFT:
                        default:
                            childLeft = lp.leftMargin + getPaddingLeft();
                    }

                    switch (verticalGravity) {
                        case Gravity.TOP:
                            childTop = lp.topMargin + getPaddingTop();
                            break;
                        case Gravity.CENTER_VERTICAL:
                            childTop = ((b - paddingBottom) - t - height) / 2 + lp.topMargin - lp.bottomMargin;
                            break;
                        case Gravity.BOTTOM:
                            childTop = ((b - paddingBottom) - t) - height - lp.bottomMargin;
                            break;
                        default:
                            childTop = lp.topMargin;
                    }

                    if (commentTextView != null && commentTextView.isPopupView(child)) {
                        if (AndroidUtilities.isTablet()) {
                            childTop = getMeasuredHeight() - child.getMeasuredHeight();
                        } else {
                            childTop = getMeasuredHeight() + keyboardSize - child.getMeasuredHeight();
                        }
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }

                notifyHeightChanged();
            }

            @Override
            public void requestLayout() {
                if (ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        sizeNotifierFrameLayout.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        fragmentView = sizeNotifierFrameLayout;

        emptyView = new LinearLayout(context);
        emptyView.setOrientation(LinearLayout.VERTICAL);
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setVisibility(View.GONE);
        sizeNotifierFrameLayout.addView(emptyView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        emptyView.setOnTouchListener((v, event) -> true);

        emptyImageView = new ImageView(context);
        emptyImageView.setImageResource(R.drawable.music_empty); //CHANGE
        emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogEmptyImage), PorterDuff.Mode.SRC_IN));
        emptyView.addView(emptyImageView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        emptyTitleTextView = new TextView(context);
        emptyTitleTextView.setTextColor(Theme.getColor(Theme.key_dialogEmptyText));
        emptyTitleTextView.setGravity(Gravity.CENTER);
        emptyTitleTextView.setTypeface(AndroidUtilities.bold());
        emptyTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        emptyTitleTextView.setPadding(AndroidUtilities.dp(40), 0, AndroidUtilities.dp(40), 0);
        emptyView.addView(emptyTitleTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 0, 11, 0, 0));

        emptySubtitleTextView = new TextView(context);
        emptySubtitleTextView.setTextColor(Theme.getColor(Theme.key_dialogEmptyText));
        emptySubtitleTextView.setGravity(Gravity.CENTER);
        emptySubtitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        emptyView.addView(emptySubtitleTextView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER, 0, 6, 0, 0));

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setEmptyView(emptyView);
        listView.setClipToPadding(false);
        listView.setAdapter(listAdapter = new ListAdapter(context));
        listView.setPadding(0, 0, 0, AndroidUtilities.dp(48));
        sizeNotifierFrameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                scrolling = newState != RecyclerView.SCROLL_STATE_IDLE;
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                }
            }
        });

        listView.setOnItemClickListener((view, position) -> {
            ListItem item;
            item = listAdapter.getItem(position);
            if (item == null) {
                return;
            }
            File file = item.file;
            if (file == null) {
                if (item.icon == R.drawable.files_gallery) {
                    HashMap<Object, Object> selectedPhotos = new HashMap<>();
                    ArrayList<Object> selectedPhotosOrder = new ArrayList<>();

                    PhotoPickerActivity fragment = new PhotoPickerActivity(0, MediaController.allMediaAlbumEntry, selectedPhotos, selectedPhotosOrder, 0, chatActivity != null, chatActivity, false);
                    fragment.setDocumentsPicker(true);
                    fragment.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                        @Override
                        public void selectedPhotosChanged() {

                        }

                        @Override
                        public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate) {
                            removeSelfFromStack();
                            if (!canceled) {
                                sendSelectedPhotos(selectedPhotos, selectedPhotosOrder, notify, scheduleDate);
                            }
                        }

                        @Override
                        public void onCaptionChanged(CharSequence text) {

                        }

                        @Override
                        public void onOpenInPressed() {
                            removeSelfFromStack();
                            delegate.startDocumentSelectActivity();
                        }
                    });
                    fragment.setMaxSelectedPhotos(maxSelectedFiles, false);
                    presentFragment(fragment);
                } else if (item.icon == R.drawable.files_music) {
                    if (delegate != null) {
                        delegate.startMusicSelectActivity();
                    }
                } else {
                    HistoryEntry he = listAdapter.history.remove(listAdapter.history.size() - 1);
                    actionBar.setTitle(he.title);
                    if (he.dir != null) {
                        listFiles(he.dir);
                    } else {
                        listRoots();
                    }
                    layoutManager.scrollToPositionWithOffset(he.scrollItem, he.scrollOffset);
                }
            } else if (file.isDirectory()) {
                HistoryEntry he = new HistoryEntry();
                he.scrollItem = layoutManager.findLastVisibleItemPosition();
                View topView = layoutManager.findViewByPosition(he.scrollItem);
                if (topView != null) {
                    he.scrollOffset = topView.getTop();
                }
                he.dir = currentDir;
                he.title = actionBar.getTitle();
                listAdapter.history.add(he);
                if (!listFiles(file)) {
                    listAdapter.history.remove(he);
                    return;
                }
                actionBar.setTitle(item.title);
            } else {
                onItemClick(view, item);
            }
        });

        listView.setOnItemLongClickListener((view, position) -> {
            ListItem item;
            item = listAdapter.getItem(position);
            return onItemClick(view, item);
        });

        shadow = new View(context);
        shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        shadow.setTranslationY(AndroidUtilities.dp(48));
        sizeNotifierFrameLayout.addView(shadow, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 3, Gravity.BOTTOM | Gravity.LEFT, 0, 0, 0, 48));

        frameLayout2 = new FrameLayout(context);
        frameLayout2.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        frameLayout2.setVisibility(View.INVISIBLE);
        frameLayout2.setTranslationY(AndroidUtilities.dp(48));
        sizeNotifierFrameLayout.addView(frameLayout2, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, Gravity.LEFT | Gravity.BOTTOM));
        frameLayout2.setOnTouchListener((v, event) -> true);

        if (commentTextView != null) {
            commentTextView.onDestroy();
        }
        commentTextView = new EditTextEmoji(context, sizeNotifierFrameLayout, null, EditTextEmoji.STYLE_DIALOG, false);
        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = new InputFilter.LengthFilter(getMessagesController().maxCaptionLength);
        commentTextView.setFilters(inputFilters);
        commentTextView.setHint(getString(R.string.AddCaption));
        commentTextView.onResume();

        frameLayout2.addView(commentTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT, 0, 0, 84, 0));
        if (chatActivity == null) {
            commentTextView.setVisibility(View.GONE);
        }

        writeButtonContainer = new FrameLayout(context);
        writeButtonContainer.setVisibility(View.INVISIBLE);
        writeButtonContainer.setScaleX(0.2f);
        writeButtonContainer.setScaleY(0.2f);
        writeButtonContainer.setAlpha(0.0f);
        writeButtonContainer.setContentDescription(getString(R.string.Send));
        sizeNotifierFrameLayout.addView(writeButtonContainer, LayoutHelper.createFrame(60, 60, Gravity.RIGHT | Gravity.BOTTOM, 0, 0, 12, 10));

        writeButton = new ImageView(context);
        Drawable writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56), Theme.getColor(Theme.key_dialogFloatingButton), Theme.getColor(Theme.key_dialogFloatingButtonPressed));
        writeButton.setBackgroundDrawable(writeButtonDrawable);
        writeButton.setImageResource(R.drawable.list_check);

        writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogFloatingIcon), PorterDuff.Mode.SRC_IN));
        writeButton.setScaleType(ImageView.ScaleType.CENTER);
        writeButton.setOutlineProvider(new ViewOutlineProvider() {
            @SuppressLint("NewApi")
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, AndroidUtilities.dp(56), AndroidUtilities.dp(56));
            }
        });
        writeButtonContainer.addView(writeButton, LayoutHelper.createFrame(56, 56, Gravity.LEFT | Gravity.TOP, 2, 0, 0, 0));
        writeButton.setOnClickListener(v -> {
            if (chatActivity != null && chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), chatActivity.getDialogId(), this::sendSelectedFiles);
            } else {
                sendSelectedFiles(true, 0);
            }
        });
        writeButton.setOnLongClickListener(view -> {
            if (chatActivity == null) {
                return false;
            }
            TLRPC.User user = chatActivity.getCurrentUser();
            if (chatActivity.getCurrentEncryptedChat() != null) {
                return false;
            }

            if (sendPopupLayout == null) {
                sendPopupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity());
                sendPopupLayout.setAnimationEnabled(false);
                sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {

                    private final android.graphics.Rect popupRect = new android.graphics.Rect();

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                            if (sendPopupWindow != null && sendPopupWindow.isShowing()) {
                                v.getHitRect(popupRect);
                                if (!popupRect.contains((int) event.getX(), (int) event.getY())) {
                                    sendPopupWindow.dismiss();
                                }
                            }
                        }
                        return false;
                    }
                });
                sendPopupLayout.setDispatchKeyEventListener(keyEvent -> {
                    if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0 && sendPopupWindow != null && sendPopupWindow.isShowing()) {
                        sendPopupWindow.dismiss();
                    }
                });

                itemCells = new ActionBarMenuSubItem[3];
                for (int a = 0; a < 3; a++) {
                    if (a == 2 && UserObject.isUserSelf(user)) {
                        continue;
                    }
                    itemCells[a] = new ActionBarMenuSubItem(getParentActivity(), a == 0, a == 2);
                    itemCells[a].setMinimumWidth(AndroidUtilities.dp(196));

                    sendPopupLayout.addView(itemCells[a], LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 48, LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT, 0, 48 * a, 0, 0));

                    itemCells[a].setOnClickListener(v -> {
                        if (sendPopupWindow != null && sendPopupWindow.isShowing()) {
                            sendPopupWindow.dismiss();
                        }
                    });
                }

                sendPopupWindow = new ActionBarPopupWindow(sendPopupLayout, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT);
                sendPopupWindow.setAnimationEnabled(false);
                sendPopupWindow.setAnimationStyle(R.style.PopupContextAnimation2);
                sendPopupWindow.setOutsideTouchable(true);
                sendPopupWindow.setClippingEnabled(true);
                sendPopupWindow.setInputMethodMode(ActionBarPopupWindow.INPUT_METHOD_NOT_NEEDED);
                sendPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
                sendPopupWindow.getContentView().setFocusableInTouchMode(true);
            }

            sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), View.MeasureSpec.AT_MOST));
            sendPopupWindow.setFocusable(true);
            int[] location = new int[2];
            view.getLocationInWindow(location);
            sendPopupWindow.showAtLocation(view, Gravity.LEFT | Gravity.TOP, location[0] + view.getMeasuredWidth() - sendPopupLayout.getMeasuredWidth() + AndroidUtilities.dp(8), location[1] - sendPopupLayout.getMeasuredHeight() - AndroidUtilities.dp(2));
            sendPopupWindow.dimBehind();
            if (!CherrygramChatsConfig.INSTANCE.getDisableVibration()) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            }

            return false;
        });

        textPaint.setTextSize(AndroidUtilities.dp(12));
        textPaint.setTypeface(AndroidUtilities.bold());

        selectedCountView = new View(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                String text = String.format("%d", Math.max(1, selectedFiles.size()));
                int textSize = (int) Math.ceil(textPaint.measureText(text));
                int size = Math.max(AndroidUtilities.dp(16) + textSize, AndroidUtilities.dp(24));
                int cx = getMeasuredWidth() / 2;

                textPaint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBoxCheck));
                paint.setColor(Theme.getColor(Theme.key_dialogBackground));
                rect.set(cx - size / 2, 0, cx + size / 2, getMeasuredHeight());
                canvas.drawRoundRect(rect, AndroidUtilities.dp(12), AndroidUtilities.dp(12), paint);

                paint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBox));
                rect.set(cx - size / 2 + AndroidUtilities.dp(2), AndroidUtilities.dp(2), cx + size / 2 - AndroidUtilities.dp(2), getMeasuredHeight() - AndroidUtilities.dp(2));
                canvas.drawRoundRect(rect, AndroidUtilities.dp(10), AndroidUtilities.dp(10), paint);

                canvas.drawText(text, cx - textSize / 2, AndroidUtilities.dp(16.2f), textPaint);
            }
        };
        selectedCountView.setAlpha(0.0f);
        selectedCountView.setScaleX(0.2f);
        selectedCountView.setScaleY(0.2f);
        sizeNotifierFrameLayout.addView(selectedCountView, LayoutHelper.createFrame(42, 24, Gravity.RIGHT | Gravity.BOTTOM, 0, 0, -2, 9));

        listRoots();
        updateEmptyView();
        updateCountButton(0);

        return fragmentView;
    }

    private boolean onItemClick(View view, ListItem item) {
        if (item == null || item.file == null || item.file.isDirectory()) {
            return false;
        }
        String path = item.file.getAbsolutePath();
        boolean add;
        if (selectedFiles.containsKey(path)) {
            selectedFiles.remove(path);
            add = false;
        } else {
            if (!item.file.canRead()) {
                showErrorBox(getString(R.string.AccessError));
                return false;
            }
            if (sizeLimit != 0) {
                if (item.file.length() > sizeLimit) {
                    showErrorBox("FileUploadLimit");
                    return false;
                }
            }
            if (maxSelectedFiles >= 0 && selectedFiles.size() >= maxSelectedFiles) {
                showErrorBox(LocaleController.formatString(R.string.PassportUploadMaxReached, LocaleController.formatPluralString("Files", maxSelectedFiles)));
                return false;
            }
            if (item.file.length() == 0) {
                return false;
            }
            selectedFiles.put(path, item);
            add = true;
        }
        scrolling = false;
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(add, true);
        }
        updateCountButton(add ? 1 : 2);
        return true;
    }

    public void setChatActivity(ChatActivity parentFragment) {
        chatActivity = parentFragment;
    }

    public void setMaxSelectedFiles(int value) {
        maxSelectedFiles = value;
    }

    private boolean showCommentTextView(boolean show, boolean animated) {
        if (commentTextView == null) {
            return false;
        }
        if (show == (frameLayout2.getTag() != null)) {
            return false;
        }
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        frameLayout2.setTag(show ? 1 : null);
        if (commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(commentTextView.getEditText());
        }
        commentTextView.hidePopup(true);
        if (show) {
            frameLayout2.setVisibility(View.VISIBLE);
            writeButtonContainer.setVisibility(View.VISIBLE);
        }
        if (animated) {
            animatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofFloat(writeButtonContainer, View.SCALE_X, show ? 1.0f : 0.2f));
            animators.add(ObjectAnimator.ofFloat(writeButtonContainer, View.SCALE_Y, show ? 1.0f : 0.2f));
            animators.add(ObjectAnimator.ofFloat(writeButtonContainer, View.ALPHA, show ? 1.0f : 0.0f));
            animators.add(ObjectAnimator.ofFloat(selectedCountView, View.SCALE_X, show ? 1.0f : 0.2f));
            animators.add(ObjectAnimator.ofFloat(selectedCountView, View.SCALE_Y, show ? 1.0f : 0.2f));
            animators.add(ObjectAnimator.ofFloat(selectedCountView, View.ALPHA, show ? 1.0f : 0.0f));
            animators.add(ObjectAnimator.ofFloat(frameLayout2, View.TRANSLATION_Y, show ? 0 : AndroidUtilities.dp(48)));
            animators.add(ObjectAnimator.ofFloat(shadow, View.TRANSLATION_Y, show ? 0 : AndroidUtilities.dp(48)));

            animatorSet.playTogether(animators);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.setDuration(180);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(animatorSet)) {
                        if (!show) {
                            frameLayout2.setVisibility(View.INVISIBLE);
                            writeButtonContainer.setVisibility(View.INVISIBLE);
                        }
                        animatorSet = null;
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(animatorSet)) {
                        animatorSet = null;
                    }
                }
            });
            animatorSet.start();
        } else {
            writeButtonContainer.setScaleX(show ? 1.0f : 0.2f);
            writeButtonContainer.setScaleY(show ? 1.0f : 0.2f);
            writeButtonContainer.setAlpha(show ? 1.0f : 0.0f);
            selectedCountView.setScaleX(show ? 1.0f : 0.2f);
            selectedCountView.setScaleY(show ? 1.0f : 0.2f);
            selectedCountView.setAlpha(show ? 1.0f : 0.0f);
            frameLayout2.setTranslationY(show ? 0 : AndroidUtilities.dp(48));
            shadow.setTranslationY(show ? 0 : AndroidUtilities.dp(48));
            if (!show) {
                frameLayout2.setVisibility(View.INVISIBLE);
                writeButtonContainer.setVisibility(View.INVISIBLE);
            }
        }
        return true;
    }

    public void updateCountButton(int animated) {
        int count = selectedFiles.size();

        if (count == 0) {
            selectedCountView.setPivotX(0);
            selectedCountView.setPivotY(0);
            showCommentTextView(false, animated != 0);
        } else {
            selectedCountView.invalidate();
            if (!showCommentTextView(true, animated != 0) && animated != 0) {
                selectedCountView.setPivotX(AndroidUtilities.dp(21));
                selectedCountView.setPivotY(AndroidUtilities.dp(12));
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(selectedCountView, View.SCALE_X, animated == 1 ? 1.1f : 0.9f, 1.0f),
                        ObjectAnimator.ofFloat(selectedCountView, View.SCALE_Y, animated == 1 ? 1.1f : 0.9f, 1.0f));
                animatorSet.setInterpolator(new OvershootInterpolator());
                animatorSet.setDuration(180);
                animatorSet.start();
            } else {
                selectedCountView.setPivotX(0);
                selectedCountView.setPivotY(0);
            }
        }
    }

    private void sendSelectedPhotos(HashMap<Object, Object> photos, ArrayList<Object> order, boolean notify, int scheduleDate) {
        if (photos.isEmpty() || delegate == null || sendPressed) {
            return;
        }
        sendPressed = true;
        ArrayList<SendMessagesHelper.SendingMediaInfo> media = new ArrayList<>();
        for (int a = 0; a < order.size(); a++) {
            Object object = photos.get(order.get(a));
            SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
            media.add(info);
            if (object instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                if (photoEntry.isVideo) {
                    info.path = photoEntry.path;
                    info.videoEditedInfo = photoEntry.editedInfo;
                } else if (photoEntry.imagePath != null) {
                    info.path = photoEntry.imagePath;
                } else if (photoEntry.path != null) {
                    info.path = photoEntry.path;
                }
                info.isVideo = photoEntry.isVideo;
                info.caption = photoEntry.caption != null ? photoEntry.caption.toString() : null;
                info.entities = photoEntry.entities;
                info.masks = !photoEntry.stickers.isEmpty() ? new ArrayList<>(photoEntry.stickers) : null;
                info.ttl = photoEntry.ttl;
            }
        }
        delegate.didSelectPhotos(media, notify, scheduleDate);
    }

    private void sendSelectedFiles(boolean notify, int scheduleDate) {
        if (selectedFiles.size() == 0 || delegate == null || sendPressed) {
            return;
        }
        sendPressed = true;
        ArrayList<String> files = new ArrayList<>(selectedFiles.keySet());
        delegate.didSelectFiles(BackupFileImportActivity.this, files, commentTextView.getText().toString(), notify, scheduleDate);
        finishFragment();
    }

    public void loadRecentFiles() {
        try {
            File[] files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
            for (int a = 0; a < files.length; a++) {
                File file = files[a];
                if (file.isDirectory()) {
                    continue;
                }
                ListItem item = new ListItem();
                item.title = file.getName();
                item.file = file;
                String fname = file.getName();
                String[] sp = fname.split("\\.");
                item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                item.subtitle = AndroidUtilities.formatFileSize(file.length());
                fname = fname.toLowerCase();
                if (fname.endsWith(".jpg") || fname.endsWith(".png") || fname.endsWith(".gif") || fname.endsWith(".jpeg")) {
                    item.thumb = file.getAbsolutePath();
                }
                listAdapter.recentItems.add(item);
            }
            sortRecentItems();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void sortRecentItems() {
        Collections.sort(listAdapter.recentItems, (o1, o2) -> {
            if (sortByName) {
                String lm = o1.file.getName();
                String rm = o2.file.getName();
                return lm.compareToIgnoreCase(rm);
            } else {
                long lm = o1.file.lastModified();
                long rm = o2.file.lastModified();
                return Long.compare(rm, lm);
            }
        });
    }

    private void sortFileItems() {
        if (currentDir == null) {
            return;
        }
        Collections.sort(listAdapter.items, (lhs, rhs) -> {
            if (lhs.file == null) {
                return -1;
            } else if (rhs.file == null) {
                return 1;
            }
            boolean isDir1 = lhs.file.isDirectory();
            boolean isDir2 = rhs.file.isDirectory();
            if (isDir1 != isDir2) {
                return isDir1 ? -1 : 1;
            } else if (isDir1 || sortByName) {
                return lhs.file.getName().compareToIgnoreCase(rhs.file.getName());
            } else {
                long lm = lhs.file.lastModified();
                long rm = rhs.file.lastModified();
                return Long.compare(rm, lm);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (commentTextView != null) {
            commentTextView.onResume();
        }
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void updateEmptyView() {
        emptyTitleTextView.setText("NoFilesFound");
        emptySubtitleTextView.setText("NoFilesInfo");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setPadding(0, 0, 0, 0);
        emptySubtitleTextView.setPadding(AndroidUtilities.dp(40), 0, AndroidUtilities.dp(40), AndroidUtilities.dp(128));
        listView.setEmptyView(emptyView);
    }

    private boolean canClosePicker() {
        if (listAdapter.history.size() > 0) {
            HistoryEntry he = listAdapter.history.remove(listAdapter.history.size() - 1);
            actionBar.setTitle(he.title);
            if (he.dir != null) {
                listFiles(he.dir);
            } else {
                listRoots();
            }
            layoutManager.scrollToPositionWithOffset(he.scrollItem, he.scrollOffset);
            return false;
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        if (commentTextView != null && commentTextView.isPopupShowing()) {
            commentTextView.hidePopup(true);
            return false;
        }
        if (!canClosePicker()) {
            return false;
        }
        return super.onBackPressed();
    }

    public void setDelegate(DocumentSelectActivityDelegate delegate) {
        this.delegate = delegate;
    }

    private boolean listFiles(File dir) {
        hasFiles = false;
        if (!dir.canRead()) {
            if (dir.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString())
                    || dir.getAbsolutePath().startsWith("/sdcard")
                    || dir.getAbsolutePath().startsWith("/mnt/sdcard")) {
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                        && !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                    currentDir = dir;
                    listAdapter.items.clear();
                    AndroidUtilities.clearDrawableAnimation(listView);
                    scrolling = true;
                    listAdapter.notifyDataSetChanged();
                    return true;
                }
            }
            showErrorBox(getString(R.string.AccessError));
            return false;
        }
        File[] files;
        try {
            files = dir.listFiles();
        } catch (Exception e) {
            showErrorBox(e.getLocalizedMessage());
            return false;
        }
        if (files == null) {
            showErrorBox(getString(R.string.UnknownError));
            return false;
        }
        currentDir = dir;
        listAdapter.items.clear();
        for (int a = 0; a < files.length; a++) {
            File file = files[a];
            if (file.getName().indexOf('.') == 0) {
                continue;
            }
            ListItem item = new ListItem();
            item.title = file.getName();
            item.file = file;
            if (file.isDirectory()) {
                item.icon = R.drawable.files_folder;
                item.subtitle = getString(R.string.Folder);
            } else {
                hasFiles = true;
                String fname = file.getName();
                String[] sp = fname.split("\\.");
                item.ext = sp.length > 1 ? sp[sp.length - 1] : "?";
                item.subtitle = AndroidUtilities.formatFileSize(file.length());
                fname = fname.toLowerCase();
                if (fname.endsWith(".jpg") || fname.endsWith(".png") || fname.endsWith(".gif") || fname.endsWith(".jpeg")) {
                    item.thumb = file.getAbsolutePath();
                }
            }
            listAdapter.items.add(item);
        }
        ListItem item = new ListItem();
        item.title = "..";
        if (listAdapter.history.size() > 0) {
            HistoryEntry entry = listAdapter.history.get(listAdapter.history.size() - 1);
            if (entry.dir == null) {
                item.subtitle = getString(R.string.Folder);
            } else {
                item.subtitle = entry.dir.toString();
            }
        } else {
            item.subtitle = getString(R.string.Folder);
        }
        item.icon = R.drawable.files_folder;
        item.file = null;
        listAdapter.items.add(0, item);
        sortFileItems();
        AndroidUtilities.clearDrawableAnimation(listView);
        scrolling = true;
        listAdapter.notifyDataSetChanged();
        return true;
    }

    private void showErrorBox(String error) {
        if (getParentActivity() == null) {
            return;
        }
        new AlertDialog.Builder(getParentActivity())
                .setTitle(getString(R.string.CG_AppName))
                .setMessage(error)
                .setPositiveButton(getString(R.string.OK), null)
                .show();
    }

    @SuppressLint("NewApi")
    private void listRoots() {
        currentDir = null;
        hasFiles = false;
        listAdapter.items.clear();

        HashSet<String> paths = new HashSet<>();
        String defaultPath = Environment.getExternalStorageDirectory().getPath();
        String defaultPathState = Environment.getExternalStorageState();
        if (defaultPathState.equals(Environment.MEDIA_MOUNTED) || defaultPathState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            ListItem ext = new ListItem();
            ext.title = getString(R.string.InternalStorage);
            ext.icon = R.drawable.files_storage;
            ext.subtitle = getString(R.string.InternalFolderInfo);
            ext.file = Environment.getExternalStorageDirectory();
            listAdapter.items.add(ext);
            paths.add(defaultPath);
        }

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/mounts"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("vfat") || line.contains("/mnt")) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d(line);
                    }
                    StringTokenizer tokens = new StringTokenizer(line, " ");
                    String path = tokens.nextToken();
                    if (paths.contains(path)) {
                        continue;
                    }
                    if (line.contains("/dev/block/vold")) {
                        if (!line.contains("/mnt/secure") && !line.contains("/mnt/asec") && !line.contains("/mnt/obb") && !line.contains("/dev/mapper") && !line.contains("tmpfs")) {
                            if (!new File(path).isDirectory()) {
                                int index = path.lastIndexOf('/');
                                if (index != -1) {
                                    String newPath = "/storage/" + path.substring(index + 1);
                                    if (new File(newPath).isDirectory()) {
                                        path = newPath;
                                    }
                                }
                            }
                            paths.add(path);
                        }
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }

        ListItem fs;
        try {
            File telegramPath = new File(ApplicationLoader.applicationContext.getExternalFilesDir(null), "Telegram");
            if (telegramPath.exists()) {
                fs = new ListItem();
                fs.title = "Telegram";
                fs.subtitle = getString(R.string.AppFolderInfo);
                fs.icon = R.drawable.files_folder;
                fs.file = telegramPath;
                listAdapter.items.add(fs);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }

        if (!listAdapter.recentItems.isEmpty()) {
            hasFiles = true;
        }

        AndroidUtilities.clearDrawableAnimation(listView);
        scrolling = true;
        listAdapter.notifyDataSetChanged();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private ArrayList<ListItem> items = new ArrayList<>();
        private ArrayList<HistoryEntry> history = new ArrayList<>();
        private ArrayList<ListItem> recentItems = new ArrayList<>();


        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 0;
        }

        @Override
        public int getItemCount() {
            int count = items.size();
            if (history.isEmpty() && !recentItems.isEmpty()) {
                count += recentItems.size() + 1;
            }
            return count;
        }

        public ListItem getItem(int position) {
            int itemsSize = items.size();
            if (position < itemsSize) {
                return items.get(position);
            } else if (history.isEmpty() && !recentItems.isEmpty() && position != itemsSize && position != itemsSize + 1) {
                position -= items.size() + 2;
                if (position < recentItems.size()) {
                    return recentItems.get(position);
                }
            }
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            int itemsSize = items.size();
            if (position == itemsSize) {
                return 2;
            } else if (position == itemsSize + 1) {
                return 0;
            }
            return 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(mContext);
                    break;
                case 1:
                    view = new SharedDocumentCell(mContext, SharedDocumentCell.VIEW_TYPE_PICKER);
                    break;
                case 2:
                    view = new ShadowSectionCell(mContext);
                    Drawable drawable = Theme.getThemedDrawableByKey(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(getThemedColor(Theme.key_windowBackgroundGray)), drawable);
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                    break;
                case 3:
                default:
                    view = new View(mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 1) {
                ListItem item = getItem(position);
                SharedDocumentCell documentCell = (SharedDocumentCell) holder.itemView;
                if (item.icon != 0) {
                    documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, null, null, item.icon, position != items.size() - 1);
                } else {
                    String type = item.ext.toUpperCase().substring(0, Math.min(item.ext.length(), 4));
                    documentCell.setTextAndValueAndTypeAndThumb(item.title, item.subtitle, type, item.thumb, 0, false);
                }
                if (item.file != null) {
                    documentCell.setChecked(selectedFiles.containsKey(item.file.toString()), !scrolling);
                } else {
                    documentCell.setChecked(false, !scrolling);
                }
            }
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogBackground));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_dialogButtonSelector));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_chat_messagePanelHint));

        themeDescriptions.add(new ThemeDescription(emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogEmptyImage));
        themeDescriptions.add(new ThemeDescription(emptyTitleTextView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogEmptyText));
        themeDescriptions.add(new ThemeDescription(emptySubtitleTextView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogEmptyText));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogBackground));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"dateTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkbox));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{SharedDocumentCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_checkboxCheck));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, null, null, null, Theme.key_files_folderIcon));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{SharedDocumentCell.class}, new String[]{"thumbImageView"}, null, null, null, Theme.key_files_folderIconBackground));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{SharedDocumentCell.class}, new String[]{"extTextView"}, null, null, null, Theme.key_files_iconText));

        themeDescriptions.add(new ThemeDescription(writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_dialogFloatingIcon));
        themeDescriptions.add(new ThemeDescription(writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_dialogFloatingButton));
        themeDescriptions.add(new ThemeDescription(writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_dialogFloatingButtonPressed));

        return themeDescriptions;
    }
}
