package com.example.xml_app.utils.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.xml_app.api.ProductApi
import com.example.xml_app.models.Product
import retrofit2.HttpException

class RecommendedProductsPagingSource(
    val productApiService: ProductApi
) : PagingSource<Int, Product>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val currentPage = params.key ?: 1

        return try {
            val response =
                productApiService.getProducts(
                    page = currentPage,
                    category = null,
                    pageSize = 4
                )
            val products = response.body() ?: emptyList()
            LoadResult.Page(
                data = products,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (products.size < 4) null else currentPage + 1
            )
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.let { page ->
                page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
            }
        }
    }

}