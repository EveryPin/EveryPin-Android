package everypin.app.network.model.kakao

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KakaoLocalSearchKeywordResponse(
    @SerialName(value = "documents")
    val documents: List<Document>?,
    @SerialName(value = "meta")
    val meta: Meta?
) {
    @Serializable
    data class Document(
        @SerialName(value = "address_name")
        val addressName: String?,
        @SerialName(value = "category_group_code")
        val categoryGroupCode: String?,
        @SerialName(value = "category_group_name")
        val categoryGroupName: String?,
        @SerialName(value = "category_name")
        val categoryName: String?,
        @SerialName(value = "distance")
        val distance: String?,
        @SerialName(value = "id")
        val id: String?,
        @SerialName(value = "phone")
        val phone: String?,
        @SerialName(value = "place_name")
        val placeName: String?,
        @SerialName(value = "place_url")
        val placeUrl: String?,
        @SerialName(value = "road_address_name")
        val roadAddressName: String?,
        @SerialName(value = "x")
        val x: String?,
        @SerialName(value = "y")
        val y: String?
    )

    @Serializable
    data class Meta(
        @SerialName(value = "is_end")
        val isEnd: Boolean?,
        @SerialName(value = "pageable_count")
        val pageableCount: Int?,
        @SerialName(value = "same_name")
        val sameName: SameName?,
        @SerialName(value = "total_count")
        val totalCount: Int?
    ) {
        @Serializable
        data class SameName(
            @SerialName(value = "keyword")
            val keyword: String?,
            @SerialName(value = "region")
            val region: List<String>?,
            @SerialName(value = "selected_region")
            val selectedRegion: String?
        )
    }
}