package uz.unnarsx.cherrygram.preferences;

import static org.telegram.messenger.LocaleController.getString;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;

import uz.unnarsx.cherrygram.core.configs.CherrygramDebugConfig;

public class BlurPreferencesBottomSheet extends BottomSheet {

    BaseFragment fragment;
    private TextView blurRadiusTextView;

    SizeNotifierFrameLayout contentView;

    public static void show(BaseFragment fragment) {
        new BlurPreferencesBottomSheet(fragment).show();
    }

    private BlurPreferencesBottomSheet(BaseFragment fragment) {
        super(fragment.getParentActivity(), false);
        this.fragment = fragment;
        if (fragment.getFragmentView() instanceof SizeNotifierFrameLayout) {
            contentView = (SizeNotifierFrameLayout) fragment.getFragmentView();
        }
        Context context = fragment.getParentActivity();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextCell forceBlur = new TextCell(context, 23, false, true, resourcesProvider);
        forceBlur.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 100, 0));
        forceBlur.setTextAndCheck(getString(R.string.BlurInChat), CherrygramDebugConfig.INSTANCE.getForceChatBlurEffect(), false);
        forceBlur.setOnClickListener(v -> {
            CherrygramDebugConfig.INSTANCE.toggleForceChatBlurEffect();
            forceBlur.setChecked(!forceBlur.isChecked());
            contentView.invalidateBlur();
            contentView.invalidateBlurredViews();
        });
        linearLayout.addView(forceBlur);

        blurRadiusTextView = new TextView(context);
        blurRadiusTextView.setText(getString(R.string.AP_DrawerBlurIntensity) + ": " + CherrygramDebugConfig.INSTANCE.getForceChatBlurEffectIntensity());
        blurRadiusTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        blurRadiusTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        blurRadiusTextView.setLines(1);
        blurRadiusTextView.setMaxLines(1);
        blurRadiusTextView.setSingleLine(true);
        blurRadiusTextView.setGravity((LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP);
        linearLayout.addView(blurRadiusTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP, 21, 13, 21, 0));

        BlurPreferencesBottomSheet.BlurIntensityCell blurIntensityCell = new BlurPreferencesBottomSheet.BlurIntensityCell(context) {
            @Override
            protected void onBlurIntensityChange(int percentage, boolean layout) {
                super.onBlurIntensityChange(percentage, layout);
                CherrygramDebugConfig.INSTANCE.setForceChatBlurEffectIntensity(percentage);
                blurRadiusTextView.setText(getString(R.string.AP_DrawerBlurIntensity) + ": " + percentage);
            }
        };
        linearLayout.addView(blurIntensityCell, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 38, 0, 5, 4, 5, 0));

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }

    private class BlurIntensityCell extends FrameLayout {

        private final SeekBarView sizeBar;
        private final int startIntensity = 0;
        private final int endIntensity = 255;

        private final TextPaint textPaint;

        public BlurIntensityCell(Context context) {
            super(context);

            setWillNotDraw(false);

            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(AndroidUtilities.dp(16));

            sizeBar = new SeekBarView(context);
            sizeBar.setReportChanges(true);
            sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                @Override
                public void onSeekBarDrag(boolean stop, float progress) {
                    onBlurIntensityChange(Math.round(startIntensity + (endIntensity - startIntensity) * progress), false);
                    contentView.invalidateBlur();
                    contentView.invalidateBlurredViews();
                }

                @Override
                public void onSeekBarPressed(boolean pressed) {
                    contentView.invalidateBlurredViews();
                }

                @Override
                public CharSequence getContentDescription() {
                    return String.valueOf(Math.round(startIntensity + (endIntensity - startIntensity) * sizeBar.getProgress()));
                }

                @Override
                public int getStepsCount() {
                    return endIntensity - startIntensity;
                }
            });
            sizeBar.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
            addView(sizeBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 38, 0, 5, 4, 5, 0));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), heightMeasureSpec);
            sizeBar.setProgress((CherrygramDebugConfig.INSTANCE.getForceChatBlurEffectIntensity() - startIntensity) / (float) (endIntensity - startIntensity));
        }

        @Override
        public void invalidate() {
            super.invalidate();
            sizeBar.invalidate();
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, info);
        }

        @Override
        public boolean performAccessibilityAction(int action, Bundle arguments) {
            return super.performAccessibilityAction(action, arguments) || sizeBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, action, arguments);
        }

        protected void onBlurIntensityChange(int percentage, boolean layout) {
        }
    }

}
