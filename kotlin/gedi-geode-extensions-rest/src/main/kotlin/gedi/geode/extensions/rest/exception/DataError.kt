package gedi.geode.extensions.rest.exception

import java.util.Calendar

data class DataError (var timestamp : String = Calendar.getInstance().time.toString(),
	var message : String? = null,
	var stackTrace : String? = null,
	var error : String? = null,
	var  path : String? = null)
