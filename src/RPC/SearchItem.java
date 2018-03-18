package RPC;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		//Create a PrintWriter from response such that we can add data to response.
		// allow access only if session exists
				HttpSession session = request.getSession(false);
				if (session == null) {
					response.setStatus(403);
					return;
				}
		double lat = Double.parseDouble(request.getParameter("lat")) ; //frontend request has lat as parameter
		double lon = Double.parseDouble(request.getParameter("lon")) ;
		String term = request.getParameter("term") ;
		String userId = request.getParameter("user_id");

		DBConnection connection = DBConnectionFactory.getDBConnection();
		Set<String> favorite = connection.getFavoriteItemIds(userId);

		List<Item> items = connection.searchItems(lat, lon, term);

		
		
		JSONArray array = new JSONArray() ;
		for (Item item : items) {
			JSONObject obj = item.toJSONObject() ;
			// Check if this is a favorite one.
			// This field is required by frontend to correctly display favorite items.
			try {
				obj.put("favorite", favorite.contains(item.getItemId()));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			array.put(obj) ;
		}

	
		RpcHelper.writeJsonArray(response, array);
		


	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
