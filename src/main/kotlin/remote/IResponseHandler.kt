package remote

interface IResponseHandler<D, R: IResponse<D>> {
    fun onResponse(response: R)
}