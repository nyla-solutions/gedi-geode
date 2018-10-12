package gedi.geode.extensions.rest

import gedi.geode.extensions.rest.exception.DataError
import gedi.solutions.geode.client.GeodeClient
import gedi.solutions.geode.io.Querier
import org.apache.geode.cache.RegionDestroyedException
import org.apache.geode.cache.client.ServerOperationException
import org.apache.geode.pdx.JSONFormatter
import org.apache.geode.pdx.PdxInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
* Query service REST wrapper
*/
@RestController
@RequestMapping("/query")
open class GeodeQueryServiceRestService
{
    @Autowired
    lateinit var geode : GeodeClient


    /**
     * The default query with no limit
     * @param query the query
     */
    @PostMapping(path = arrayOf("/"),produces = arrayOf("application/json"))
    fun query(@RequestBody query: String?) : String?
    {
        return queryLimit(0,query);
    }//-----------------------------------------------------------

    @PostMapping(path = arrayOf("{limit}"),produces = arrayOf("application/json"))
    fun queryLimit(@PathVariable limit : Int, @RequestBody query: String?) : String?
    {

        try {

            var results : Collection<PdxInstance>? = Querier.query(query);


            if(results == null)
                return null;

            var responseJson = StringBuilder();

            var count = 0;


            if(limit > 0){
                for (pdxInstance : PdxInstance in results) {

                    responseJson.append(JSONFormatter.toJSON(pdxInstance));

                    count++;

                    if(count >= limit )
                        break;

                }
            }
            else{
                for (pdxInstance : PdxInstance in results) {


                    if(count > 0 )
                        responseJson.append(",");

                    responseJson.append(JSONFormatter.toJSON(pdxInstance));


                    count++;

                }
            }

            var allResults = StringBuilder("[").append(responseJson).append("]");

            return allResults.toString();
        }
        catch(e: Exception )
        {
            e.printStackTrace();
            throw e;
        }
    }//------------------------------------------------

    /**
     * HAndling exceptions in general for REST responses
     * @param request the HTTP request
     * @param response the HTTP reponse
     * @param e the exception
     * @return Data Error details
     */
    @ExceptionHandler(Exception::class)
    fun  handleException(request : HttpServletRequest, response : HttpServletResponse, e : Exception) : DataError
    {

        val cause = e.cause;

        var dataError = DataError();
        dataError.path = request.getRequestURI();

        dataError.error = "Processing error";

        if(e is ServerOperationException)
        {
            dataError.error ="Server opertion error";

            if(cause is RegionDestroyedException)
            {
                var regionDestroyedException = cause;
                dataError.message = "Region region:"+regionDestroyedException.getRegionFullPath()+" not found";
            }
        }
        response.status = 500;

        dataError.message = e.message;

        return dataError;
    }//------------------------------------------------
}