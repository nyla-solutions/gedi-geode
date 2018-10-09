package gedi.solutions.geode.rest.app.exception;

public class DataError
{
	private String message;
	private String stackTrace;
	private String error;
	private String  path;
	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}
	/**
	 * @return the stackTrace
	 */
	public String getStackTrace()
	{
		return stackTrace;
	}
	/**
	 * @return the error
	 */
	public String getError()
	{
		return error;
	}
	/**
	 * @return the path
	 */
	public String getPath()
	{
		return path;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}
	/**
	 * @param stackTrace the stackTrace to set
	 */
	public void setStackTrace(String stackTrace)
	{
		this.stackTrace = stackTrace;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(String error)
	{
		this.error = error;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path)
	{
		this.path = path;
	}
	
	
}
