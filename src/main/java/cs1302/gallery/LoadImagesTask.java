package cs1302.gallery;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import javafx.concurrent.Task;

/**
 * Task created for loading the images for grid pane by querying 
 * the iTunes search API. It will query by constructing the URL based on
 * searchQuery provided by user.
 * 
 * LoadImagesTask is extending Task<T> class, where parameter type T has been 
 * replaced with List<String> as we want to return list of image URL's gathered by
 * querying iTunes search API.
 *
 */
public class LoadImagesTask extends Task<List<String>> {

	private String searchQuery;
	
	public LoadImagesTask(String searchQuery) {
		super();
		this.searchQuery = searchQuery;
	}

	/* (non-Javadoc)
	 * @see javafx.concurrent.Task#call()
	 * 
	 * Overriden call() method which is doing the task of 
	 * querying the iTunes search API and returning the list of image URL's from response
	 */
	@Override
	protected List<String> call() throws Exception {
		
		//taken complete unit of work as 500 for progressBar update
		updateProgress(0, 500);
		List<String> imageURLList = new ArrayList<>();
		String[] searchQuerySplit = this.searchQuery.split(GalleryAppConstants.SPLIT_REGEX); //split searchQuery string by one or more spaces
		StringBuffer search = new StringBuffer();
		for (int i= 0; i < searchQuerySplit.length - 1; i++) {
			search.append(searchQuerySplit[i]).append(GalleryAppConstants.PLUS); //append plus symbol to each of the strings
		}
		search.append(searchQuerySplit[searchQuerySplit.length - 1]);
		StringBuffer finalUrl = new StringBuffer();
		finalUrl.append(GalleryAppConstants.URL_END_POINT).append(GalleryAppConstants.PARAMETER_TERM)
							.append(GalleryAppConstants.EQUALS).append(search); //final URL for iTunes search query
		updateProgress(30, 500);
		URL url = null;
		InputStreamReader reader = null;
		updateProgress(60, 500);
		try {
			url = new URL(new String(finalUrl));
			updateProgress(80, 500);
			reader = new InputStreamReader(url.openStream()); //querying iTunes search API
			updateProgress(150, 500);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		updateProgress(250, 500);
		Gson gson = new Gson();
		Output output = gson.fromJson(reader, Output.class); //parse response of iTunes to get Java object
		updateProgress(350, 500);
		output.getResults().forEach(result -> imageURLList.add(result.getArtworkUrl100())); //fetch artWorkURL100 string from response
		updateProgress(400, 500);
		List<String> distinctUrlList = imageURLList.stream().distinct().collect(Collectors.toList()); //filter distinct URL's
		updateProgress(500, 500);
        return distinctUrlList;
	}
}
