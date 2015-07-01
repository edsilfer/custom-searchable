package br.com.customsearchable.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by edgar on 6/27/15.
 */
public class ManifestParser {
    // Constants
    public static final String TAG = "ManifestParser";

    public static final String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";
    public static final String ANDROID_MANIFEST_FILENAME = "AndroidManifest.xml";

    public static final String ELEMENT_MANIFEST = "manifest";
    public static final String ELEMENT_ACTIVITY = "activity";
    public static final String ELEMENT_PROVIDER = "provider";
    public static final String ELEMENT_INTENT_FILTER = "intent-filter";
    public static final String ELEMENT_ACTION = "action";

    public static final String PROPERTY_MANIFEST_PACKAGE = "package";
    public static final String PROPERTY_ACTIVITY_NAME = "name";

    public static final String PROPERTY_PROVIDER_NAME = "name";
    public static final String PROPERTY_PROVIDER_AUTHORITY = "authorities";

    public static final String PROPERTY_ACTION_NAME = "name";
    public static final String SEARCH_INTENT_FILTER = "android.intent.action.SEARCH";

    public static String getAppPackage (Context context) {
        try {
            AssetManager am = context.createPackageContext(context.getApplicationContext().getPackageName(), 0).getAssets();
            XmlResourceParser xrp = am.openXmlResourceParser("AndroidManifest.xml");

            xrp.next();
            int eventType = xrp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase(ELEMENT_MANIFEST)) {
                    String packageName = xrp.getAttributeValue(null, PROPERTY_MANIFEST_PACKAGE);

                    return packageName;
                }

                eventType = xrp.next();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getSearchableActivity (Context context) {
        try {
            AssetManager am = context.createPackageContext(context.getApplicationContext().getPackageName(), 0).getAssets();
            XmlResourceParser xrp = am.openXmlResourceParser("AndroidManifest.xml");

            xrp.next();
            int eventType = xrp.getEventType();

            String activityName = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase(ELEMENT_ACTIVITY)) {
                    activityName = xrp.getAttributeValue(ANDROID_NAMESPACE, PROPERTY_ACTIVITY_NAME);
                }

                if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase(ELEMENT_ACTION)) {
                    String intentFilter = xrp.getAttributeValue(ANDROID_NAMESPACE, PROPERTY_ACTION_NAME);

                    if (SEARCH_INTENT_FILTER.equals(intentFilter)) {
                        return activityName;
                    }
                }
                eventType = xrp.next();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Map<String, String> getProviderNameAndAuthority (Context context) {
        try {
            Map<String, String> providers = new HashMap<>();

            AssetManager am = context.createPackageContext(context.getApplicationContext().getPackageName(), 0).getAssets();
            XmlResourceParser xrp = am.openXmlResourceParser(ANDROID_MANIFEST_FILENAME);

            xrp.next();
            int eventType = xrp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase(ELEMENT_PROVIDER)) {
                    String providerName = xrp.getAttributeValue(ANDROID_NAMESPACE, PROPERTY_PROVIDER_NAME);
                    String providerAuthority = xrp.getAttributeValue(ANDROID_NAMESPACE, PROPERTY_PROVIDER_AUTHORITY);

                    providers.put(providerName, providerAuthority);
                }

                eventType = xrp.next();
            }

            return providers;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return null;
    }
}
