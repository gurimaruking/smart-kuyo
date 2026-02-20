package com.example.smartkuyo.domain.model

enum class SutraType(
    val displayNameJa: String,
    val denomination: Denomination?,
    val description: String
) {
    // 仏教共通
    HANNYA_SHINGYO(
        "般若心経",
        null,
        "仏教各宗派で広く読まれるお経"
    ),

    // 浄土宗・浄土真宗
    NAMU_AMIDA_BUTSU(
        "南無阿弥陀仏（念仏）",
        null,
        "浄土系宗派の基本の念仏"
    ),

    // 浄土真宗
    SHOSHINGE(
        "正信偈",
        Denomination.JODO_SHINSHU,
        "浄土真宗の重要なお経"
    ),

    // 日蓮宗
    NAMU_MYOHO_RENGE_KYO(
        "南無妙法蓮華経（題目）",
        Denomination.NICHIREN,
        "日蓮宗の基本の題目"
    ),

    // 真言宗
    KOMYO_SHINGON(
        "光明真言",
        Denomination.SHINGON,
        "真言宗の代表的な真言"
    ),

    // 曹洞宗
    SHUSHOGI(
        "修証義（第一章〜第三章）",
        Denomination.SOTO,
        "曹洞宗の教えをまとめたお経（前半）"
    ),
    SHUSHOGI_PART2(
        "修証義（第四章〜第五章）",
        Denomination.SOTO,
        "曹洞宗の教えをまとめたお経（後半）"
    ),

    // 臨済宗
    DAIHI_SHU(
        "大悲呪",
        Denomination.RINZAI,
        "臨済宗で広く読まれるお経"
    ),

    // 天台宗（般若心経が共通で使える）

    // キリスト教
    LORD_PRAYER(
        "主の祈り",
        Denomination.CATHOLIC,
        "キリスト教の基本的な祈り"
    ),
    AVE_MARIA(
        "アヴェ・マリア",
        Denomination.CATHOLIC,
        "カトリックの聖母マリアへの祈り"
    ),
    AMAZING_GRACE(
        "アメイジング・グレイス",
        Denomination.PROTESTANT,
        "プロテスタントの代表的な讃美歌"
    ),

    // 神道
    NORITO(
        "祝詞",
        Denomination.SHINTO_GENERAL,
        "神道の祈りの言葉"
    );

    companion object {
        fun getForDenomination(denomination: Denomination): List<SutraType> {
            val common = entries.filter { it.denomination == null }
            val specific = entries.filter { it.denomination == denomination }
            val sameReligion = entries.filter {
                it.denomination != null &&
                    it.denomination != denomination &&
                    it.denomination.religion == denomination.religion
            }
            return (specific + common + sameReligion).distinct()
        }
    }
}
