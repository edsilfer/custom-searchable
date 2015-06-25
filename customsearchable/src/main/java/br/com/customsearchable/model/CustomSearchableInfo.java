package br.com.customsearchable.model;

import android.content.Context;

import br.com.customsearchable.R;

/**
 * Created by edgar on 6/16/15.
 */
public class CustomSearchableInfo {

    // Dimensions
    private static Float barHeight;
    private static Integer resultItemHeight;
    private static Integer resultItemHeaderTextSize;
    private static Integer resultItemSubheaderTextSize;
    private static Integer searchTextSize;

    // Icons
    private static Integer barDismissIcon;
    private static Integer barMicIcon;
    private static Integer simpleSuggestionsLeftIcon;
    private static Integer simpleSuggestionsRightIcon;

    // Colors
    private static Integer transparencyColor;
    private static Integer primaryColor;
    private static Integer textPrimaryColor;
    private static Integer textHintColor;
    private static Integer rippleEffectMaskColor;
    private static Integer rippleEffectWaveColor;

    // Other
    private static Integer searchMaxLines;
    private static Boolean isTwoLineExhibition;

    public CustomSearchableInfo (Context context) {
        // Dimensions
        barHeight =  context.getResources().getDimension(R.dimen.bar_height);
        resultItemHeight =  context.getResources().getDimensionPixelSize(R.dimen.result_item_height);
        resultItemHeaderTextSize =  context.getResources().getDimensionPixelSize(R.dimen.result_item_header_text_size);
        resultItemSubheaderTextSize =  context.getResources().getDimensionPixelSize(R.dimen.result_item_subheader_text_size);
        searchTextSize =  context.getResources().getDimensionPixelSize(R.dimen.search_text_size);

        // Icons
        barDismissIcon = R.drawable.arrow_left_icon;
        barMicIcon = R.drawable.mic_icon;
        simpleSuggestionsLeftIcon = R.drawable.clock_icon;
        simpleSuggestionsRightIcon = R.drawable.arrow_left_up_icon;

        // Colors
        transparencyColor = context.getResources().getColor(R.color.transparency_color);
        primaryColor = context.getResources().getColor(R.color.primaryColor);
        textPrimaryColor = context.getResources().getColor(R.color.textPrimaryColor);
        textHintColor = context.getResources().getColor(R.color.textHintColor);
        rippleEffectMaskColor = context.getResources().getColor(R.color.rippleEffectMaskColor);
        rippleEffectWaveColor = context.getResources().getColor(R.color.rippleEffectMaskColor);

        // Other
        searchMaxLines = 1;
        isTwoLineExhibition = Boolean.FALSE;
    }

    // Getters and Setters _________________________________________________________________________
    public static Boolean getIsTwoLineExhibition() {
        return isTwoLineExhibition;
    }

    public static void setIsTwoLineExhibition(Boolean isTwoLineExhibition) {
        CustomSearchableInfo.isTwoLineExhibition = isTwoLineExhibition;
    }

    public static Float getBarHeight() {
        return barHeight;
    }

    public static void setBarHeight(Float barHeight) {
        CustomSearchableInfo.barHeight = barHeight;
    }

    public static Integer getResultItemHeight() {
        return resultItemHeight;
    }

    public static void setResultItemHeight(Integer resultItemHeight) {
        CustomSearchableInfo.resultItemHeight = resultItemHeight;
    }

    public static Integer getResultItemHeaderTextSize() {
        return resultItemHeaderTextSize;
    }

    public static void setResultItemHeaderTextSize(Integer resultItemHeaderTextSize) {
        CustomSearchableInfo.resultItemHeaderTextSize = resultItemHeaderTextSize;
    }

    public static Integer getResultItemSubheaderTextSize() {
        return resultItemSubheaderTextSize;
    }

    public static void setResultItemSubheaderTextSize(Integer resultItemSubheaderTextSize) {
        CustomSearchableInfo.resultItemSubheaderTextSize = resultItemSubheaderTextSize;
    }

    public static Integer getSearchTextSize() {
        return searchTextSize;
    }

    public static void setSearchTextSize(Integer searchTextSize) {
        CustomSearchableInfo.searchTextSize = searchTextSize;
    }

    public static Integer getBarDismissIcon() {
        return barDismissIcon;
    }

    public static void setBarDismissIcon(Integer barDismissIcon) {
        CustomSearchableInfo.barDismissIcon = barDismissIcon;
    }

    public static Integer getBarMicIcon() {
        return barMicIcon;
    }

    public static void setBarMicIcon(Integer barMicIcon) {
        CustomSearchableInfo.barMicIcon = barMicIcon;
    }

    public static Integer getSimpleSuggestionsLeftIcon() {
        return simpleSuggestionsLeftIcon;
    }

    public static void setSimpleSuggestionsLeftIcon(Integer simpleSuggestionsLeftIcon) {
        CustomSearchableInfo.simpleSuggestionsLeftIcon = simpleSuggestionsLeftIcon;
    }

    public static Integer getSimpleSuggestionsRightIcon() {
        return simpleSuggestionsRightIcon;
    }

    public static void setSimpleSuggestionsRightIcon(Integer simpleSuggestionsRightIcon) {
        CustomSearchableInfo.simpleSuggestionsRightIcon = simpleSuggestionsRightIcon;
    }

    public static Integer getTransparencyColor() {
        return transparencyColor;
    }

    public static void setTransparencyColor(Integer transparencyColor) {
        CustomSearchableInfo.transparencyColor = transparencyColor;
    }

    public static Integer getPrimaryColor() {
        return primaryColor;
    }

    public static void setPrimaryColor(Integer primaryColor) {
        CustomSearchableInfo.primaryColor = primaryColor;
    }

    public static Integer getTextPrimaryColor() {
        return textPrimaryColor;
    }

    public static void setTextPrimaryColor(Integer textPrimaryColor) {
        CustomSearchableInfo.textPrimaryColor = textPrimaryColor;
    }

    public static Integer getTextHintColor() {
        return textHintColor;
    }

    public static void setTextHintColor(Integer textHintColor) {
        CustomSearchableInfo.textHintColor = textHintColor;
    }

    public static Integer getRippleEffectMaskColor() {
        return rippleEffectMaskColor;
    }

    public static void setRippleEffectMaskColor(Integer rippleEffectMaskColor) {
        CustomSearchableInfo.rippleEffectMaskColor = rippleEffectMaskColor;
    }

    public static Integer getRippleEffectWaveColor() {
        return rippleEffectWaveColor;
    }

    public static void setRippleEffectWaveColor(Integer rippleEffectWaveColor) {
        CustomSearchableInfo.rippleEffectWaveColor = rippleEffectWaveColor;
    }

    public static Integer getSearchMaxLines() {
        return searchMaxLines;
    }

    public static void setSearchMaxLines(Integer searchMaxLines) {
        CustomSearchableInfo.searchMaxLines = searchMaxLines;
    }
}
