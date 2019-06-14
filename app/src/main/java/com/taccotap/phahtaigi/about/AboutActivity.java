package com.taccotap.phahtaigi.about;


import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.taccotap.phahtaigi.R;

public class AboutActivity extends MaterialAboutActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_Mal_Light_LightActionBar);
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();

        // Add items to card

        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(R.string.app_name)
                .icon(R.drawable.ic_launcher)
                .build());

        try {
            appCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(context,
                    new IconicsDrawable(context, CommunityMaterial.Icon.cmd_android_debug_bridge).sizeDp(48),
                    getString(R.string.about_version_item_text),
                    false));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // gesture
        MaterialAboutCard.Builder gestureCardBuilder = new MaterialAboutCard.Builder();
        gestureCardBuilder.title("鍵盤 ê 手勢");

        gestureCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("撇去倒爿：選輸入法")
                .icon(new IconicsDrawable(context, CommunityMaterial.Icon.cmd_arrow_left).sizeDp(48))
                .build());
        gestureCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("撇去正爿：跳去下一个輸入法")
                .icon(new IconicsDrawable(context, CommunityMaterial.Icon.cmd_arrow_right).sizeDp(48))
                .build());
        gestureCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("撇去頂面：叫出設定")
                .icon(new IconicsDrawable(context, CommunityMaterial.Icon.cmd_arrow_up).sizeDp(48))
                .build());
        gestureCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("撇去下底：Kā輸入法關--起-來")
                .icon(new IconicsDrawable(context, CommunityMaterial.Icon.cmd_arrow_down).sizeDp(48))
                .build());

        // function key
        MaterialAboutCard.Builder functionCardBuilder = new MaterialAboutCard.Builder();
        functionCardBuilder.title("特殊 ê KEY");

        functionCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Kā空白key tshi̍h牢咧：選輸入法")
                .subText("Kan-na佇主要ê鍵盤有效，符號鍵盤無影響。")
                .icon(new IconicsDrawable(context, CommunityMaterial.Icon.cmd_crop_landscape).sizeDp(48))
                .build());

        functionCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Kā欲選ê字詞tshi̍h牢咧：tshuē辭典")
                .subText("直接轉過辭典ê app tshiau-tshuē字詞。")
                .icon(new IconicsDrawable(context, CommunityMaterial.Icon.cmd_crop_landscape).sizeDp(48))
                .build());

        // feedback
        MaterialAboutCard.Builder feedbackCardBuilder = new MaterialAboutCard.Builder();
        feedbackCardBuilder.title(getString(R.string.about_feedback_card_title_text));

        feedbackCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(getString(R.string.about_facebook_group_item_text))
                .subText(getString(R.string.about_facebook_group_url))
                .icon(new IconicsDrawable(context, CommunityMaterial.Icon.cmd_facebook).sizeDp(48))
                .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(this, Uri.parse(getString(R.string.about_facebook_group_url))))
                .build());
        feedbackCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("鬥 phah 五粒星！")
                .icon(new IconicsDrawable(context, CommunityMaterial.Icon.cmd_star).sizeDp(48))
                .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(this, Uri.parse("https://play.google.com/store/apps/details?id=com.taccotap.phahtaigi")))
                .build());

        // help
        MaterialAboutCard.Builder helpCardBuilder = new MaterialAboutCard.Builder();
        helpCardBuilder.title("幫贊");

        helpCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("多謝你 ê 使用")
                .subText("不管是建議，抑是會使鬥出力出錢，攏誠歡迎，請和我連絡，勞力！")
                .icon(new IconicsDrawable(context, CommunityMaterial.Icon.cmd_human).sizeDp(48))
                .build());

        // author
        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title(getString(R.string.about_author_card_title_text));

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(getString(R.string.about_author_item_text))
                .subText(getString(R.string.about_author_item_subtext))
                .icon(new IconicsDrawable(context, CommunityMaterial.Icon.cmd_account).sizeDp(48))
                .build());

        authorCardBuilder.addItem(ConvenienceBuilder.createEmailItem(context,
                new IconicsDrawable(context, CommunityMaterial.Icon.cmd_email_outline).sizeDp(48),
                getString(R.string.about_email_me_item_text),
                true,
                getString(R.string.about_feedback_email),
                getString(R.string.about_email_me_mail_title)));

        // privacy
        MaterialAboutCard.Builder privacyCardBuilder = new MaterialAboutCard.Builder();
        privacyCardBuilder.title("隱私權政策");

        privacyCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("使用app tio̍h表示你同意這ê隱私權政策")
                .icon(new IconicsDrawable(context, CommunityMaterial.Icon.cmd_account_alert).sizeDp(48))
                .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(this, Uri.parse("http://phahtaigi.blogspot.com/p/phah-tai-gi.html")))
                .build());

        return new MaterialAboutList(
                appCardBuilder.build(),
                gestureCardBuilder.build(),
                functionCardBuilder.build(),
                helpCardBuilder.build(),
                feedbackCardBuilder.build(),
                privacyCardBuilder.build(),
                authorCardBuilder.build());
    }

    @Nullable
    @Override
    protected CharSequence getActivityTitle() {
        return "關於 Phah Tâi-gí";
    }
}
