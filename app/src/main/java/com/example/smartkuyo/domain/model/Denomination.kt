package com.example.smartkuyo.domain.model

enum class Denomination(
    val religion: Religion,
    val displayNameJa: String,
    val posthumousNameLabel: String
) {
    // 仏教各宗派
    JODO_SHU(Religion.BUDDHISM, "浄土宗", "戒名"),
    JODO_SHINSHU(Religion.BUDDHISM, "浄土真宗", "法名"),
    SHINGON(Religion.BUDDHISM, "真言宗", "戒名"),
    SOTO(Religion.BUDDHISM, "曹洞宗", "戒名"),
    RINZAI(Religion.BUDDHISM, "臨済宗", "戒名"),
    NICHIREN(Religion.BUDDHISM, "日蓮宗", "法号"),
    TENDAI(Religion.BUDDHISM, "天台宗", "戒名"),
    BUDDHISM_OTHER(Religion.BUDDHISM, "その他仏教", "戒名"),

    // キリスト教
    CATHOLIC(Religion.CHRISTIANITY, "カトリック", "洗礼名"),
    PROTESTANT(Religion.CHRISTIANITY, "プロテスタント", "洗礼名"),

    // 神道
    SHINTO_GENERAL(Religion.SHINTO, "神道", "諡名"),

    // 無宗教
    NONE(Religion.NON_RELIGIOUS, "無宗教", "");

    companion object {
        fun getByReligion(religion: Religion): List<Denomination> =
            entries.filter { it.religion == religion }
    }
}
