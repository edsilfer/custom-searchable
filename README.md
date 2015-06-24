# Custom Searchable
**Currently this library is under construction and not ready for production**

This repository contains a library that aims to provide a custom searchable interface for android applications.

# Table of Contents
* [Introduction](#intro)
* [Showcase](#showcase)
* [How to use](#how-to-use)
* [TODO List](#to-do)
* [Team Members](#team-members)
* [License](#license)
 
# <a name="intro"></a>Introduction
Android provides developers with an out-of-the-box search interface [(link)](http://developer.android.com/guide/topics/search/search-dialog.html), however this API might not fit your app usage, mainly when it comes to layout configuration. In order to compensate this, Custom Searchable library was created. It provides easy layout configuration such as

 - **Search bar customization:** height, background color, left and rightIcons customization, search text size and color;
 - **Result list customization:** row height, ripple-effect color, left and right icon, two text lines per row with standard text customization;
 
# <a name="showcase"></a>Showcase
_Under construction_

# <a name="how-to-use"></a>How to use
The custom searchable interface is delivered to the user as an activity called Search Activity. The mechanism to use this API is based on the OOTB Searchable Interface. Said that, the developer has two options:

* **Simple Recent Suggestions:** provides a list with the recent search performed by the user;
* **Custom Suggestion:** Allows the developer to provider custom suggestions based on the information typed by the user;

<a name="implSimpleSugInt">Implementing a Simple Recent Suggestion interface
-------------------------------------------------
In order to implement a simple recent suggestion interface, follow the steps bellow:

1. Implement a SuggestioionProvider that extends the ``` SearchRecentSuggestionsProvider ```. Information about coontent providers can be found [here](http://developer.android.com/guide/topics/search/adding-custom-suggestions.html);

2. Define the  searchable activity to which the search intents will be delivered in your AndroiManifest.xml:

``` xml
<meta-data
        android:name="edsilfer.app.default_searchable"
        android:value="br.com.edsilfer.Main" />
```
PS1.: In the ```android:value``` property you **must** specify the entire path to your activity;
3. Declare your provider in in your AndroiManifest.xml:

``` xml
 <provider android:name=".content_provider.RecentSuggestionsProvider"
    android:authorities="br.com.edsilfer.content_provider.RecentSuggestionsProvider" />
```
PS2.: The ```android:authorities``` property **must** specify the entire path to your provider;

4. In your searchable activity class, add code for handling the search intent:

``` java
    // Handles the intent that carries user's choice in the Search Interface
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, RecentSuggestionsProvider.AUTHORITY, RecentSuggestionsProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Bundle bundle = this.getIntent().getExtras();
            assert (bundle != null);
            if (bundle != null) {
                ResultItem receivedItem = bundle.getParcelable(CustomSearchableConstants.CLICKED_RESULT_ITEM);

                Log.i("RI.header", receivedItem.getHeader());
                Log.i("RI.subHeader", receivedItem.getSubHeader());
                Log.i("RI.leftIcon", receivedItem.getLeftIcon().toString());
                Log.i("RI.rightIcon", receivedItem.getRightIcon().toString());
            }
        }
    }
```
5. Call the SearchActivity through ```startActivity()``` passing it an Intent;

<a name="implCustomSugInt">Implementing a Custom Suggestion interface
------------------------------------------
In order to implement a custom suggestion interface, follow the steps bellow:

1. Create a content provider that follows the column contrat specified in the file ```CustomProviderColumns```;
2. Follow the steps 1 to 5 of the section (#implSimpleSugInt);

Customizing the Searchable Interface
------------------------------------------
The class ```CustomSearchableInfo``` provides the following attributes that helps you to customize your search interface:

 * ``barHeight```: sets the search bar header height (defaul it 56dp);
 * ```resultItemHeight```: sets the result item brought in the result list height (default is 60dp);
 * ```resultItemHeaderTextSize```: sets the result item header text size (default is 16dp);
 * ```resultItemSubheaderTextSize```: sets the result item sub-header (when working with two-line mode) text size (default is 14dp);
 * ```searchTextSize```: sets the search text text size (default is 14dp);
 * ```barDismissIcon```: sets the dismiss search activity icon (return button) (default is a left point arrow);
 * ```barMicIcon```: sets the mic image that trigger the speech-to-text api (default is mic icon from Lollipop);
 * ```simpleSuggestionsLeftIcon```: sets the result item left icon (default is a clock);
 * ```simpleSuggestionsRightIcon```: sets the result item right icon (default is a arrow point left up);
 * ```transparencyColor```: sets the transparency color that will overlay the bottom activity;
 * ```primaryColor```: background color for the search bar and result row item;
 * ```textPrimaryColor```: primary text color applied to the search text, header and subheader (when working with the two-line-mode);
 * ```textHintColor```: hint text color applied to the search text;
 * ```rippleEffectMaskColor```: mask color for the ripple effect;
 * ```rippleEffectWaveColor```: wave color for the ripple effect;
 * ```isTwoLineExhibition```: sets if the result list will bring one or two lines;

In order to customize the SearchActivity layout interface with those above attributes, just call the set for them from the class ```CustomSearchableInfo```.

# <a name="to-do"></a>TODO List
* Upload library into JCenter/Maven;
* Parse icons corresponding columns from return cursor of query method;
* Implement functionalities for the columns descrobed in the ```CustomProviderColumns````contract
* Add text-complete as suggestions in the search input text;

# <a name="team-members"></a>Team Members
* "Fernandes S. Edgar" <fernandes.s.edgar@gmail.com>

# <a name="license"></a>License
Copyright 2015 Edgar da Silva Fernandes

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

