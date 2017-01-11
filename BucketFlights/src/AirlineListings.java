
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.text.View;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.qpxExpress.QPXExpressRequestInitializer;
import com.google.api.services.qpxExpress.QPXExpress;
import com.google.api.services.qpxExpress.model.FlightInfo;
import com.google.api.services.qpxExpress.model.LegInfo;
import com.google.api.services.qpxExpress.model.PassengerCounts;
import com.google.api.services.qpxExpress.model.PricingInfo;
import com.google.api.services.qpxExpress.model.SegmentInfo;
import com.google.api.services.qpxExpress.model.SliceInfo;
import com.google.api.services.qpxExpress.model.TripOption;
import com.google.api.services.qpxExpress.model.TripOptionsRequest;
import com.google.api.services.qpxExpress.model.TripsSearchRequest;
import com.google.api.services.qpxExpress.model.SliceInput;
import com.google.api.services.qpxExpress.model.TripsSearchResponse;

public class AirlineListings {

	/**
	 * @param args
	 */

	private static final String APPLICATION_NAME = "MyFlightApplication";

	private static final String API_KEY = "AIzaSyDnBCdsmTnrL5XFrO2TjJyvFioswjakNYU";

	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	// private static final QPXExpress qpXExpress = new
	// QPXExpress.Builder(httpTransport, JSON_FACTORY, null)
	// .setApplicationName(APPLICATION_NAME)
	// .setGoogleClientRequestInitializer(new
	// QPXExpressRequestInitializer(API_KEY)).build();

	public static List<TripOption> getFlight(int passengersCount, String origin, String dest, String date,
			int resultCount) {
		List<TripOption> tripResults = new ArrayList<TripOption>();
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			PassengerCounts passengers = new PassengerCounts();
			passengers.setAdultCount(passengersCount);

			List<SliceInput> slices = new ArrayList<SliceInput>();

			SliceInput slice = new SliceInput();
			slice.setOrigin(origin);
			slice.setDestination(dest);
			slice.setDate(date);
			slices.add(slice);

			TripOptionsRequest request = new TripOptionsRequest();
			request.setSolutions(resultCount);
			request.setPassengers(passengers);
			request.setSlice(slices);

			TripsSearchRequest parameters = new TripsSearchRequest();
			parameters.setRequest(request);

			QPXExpress qpXExpress = new QPXExpress.Builder(httpTransport, JSON_FACTORY, null)
					.setApplicationName(APPLICATION_NAME)
					.setGoogleClientRequestInitializer(new QPXExpressRequestInitializer(API_KEY)).build();

			TripsSearchResponse list = qpXExpress.trips().search(parameters).execute();
			tripResults = list.getTrips().getTripOption();
			return tripResults;

		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private static String getReturnDate(String startDate, int duration) {

		String endDate = "";
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		Calendar cal = Calendar.getInstance();

		try {
			date = df.parse(startDate);
			cal.setTime(date);
			cal.add(Calendar.DATE, 5);
			endDate = df.format(cal.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return endDate;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		List<TripOption> roundTripResults = getRoundTripResults(1,"IAD","JFK","2017-01-13",5, 1);
		System.out.println("SIZE: " + roundTripResults.size() + "\n");
		String id;
		double totalPrice = 0.0;

		for (int i = 0; i < roundTripResults.size(); i++) {
			// Trip Option ID
			id = roundTripResults.get(i).getId();
			System.out.println("id " + id);

			// Slice
			List<SliceInfo> sliceInfo = roundTripResults.get(i).getSlice();
			for (int j = 0; j < sliceInfo.size(); j++) {
				int duration = sliceInfo.get(j).getDuration();
				System.out.println("duration " + duration);
				List<SegmentInfo> segInfo = sliceInfo.get(j).getSegment();
				for (int k = 0; k < segInfo.size(); k++) {
					String bookingCode = segInfo.get(k).getBookingCode();
					System.out.println("bookingCode " + bookingCode);
					FlightInfo flightInfo = segInfo.get(k).getFlight();
					String flightNum = flightInfo.getNumber();
					System.out.println("flightNum " + flightNum);
					String flightCarrier = flightInfo.getCarrier();
					System.out.println("flightCarrier " + flightCarrier);
					List<LegInfo> leg = segInfo.get(k).getLeg();
					for (int l = 0; l < leg.size(); l++) {
						String aircraft = leg.get(l).getAircraft();
						System.out.println("aircraft " + aircraft);
						String arrivalTime = leg.get(l).getArrivalTime();
						System.out.println("arrivalTime " + arrivalTime);
						String departTime = leg.get(l).getDepartureTime();
						System.out.println("departTime " + departTime);
						String dest = leg.get(l).getDestination();
						System.out.println("Destination " + dest);
						String destTer = leg.get(l).getDestinationTerminal();
						System.out.println("DestTer " + destTer);
						String origin = leg.get(l).getOrigin();
						System.out.println("origun " + origin);
						String originTer = leg.get(l).getOriginTerminal();
						System.out.println("OriginTer " + originTer);
						int durationLeg = leg.get(l).getDuration();
						System.out.println("durationleg " + durationLeg);
						int mil = leg.get(l).getMileage();
						System.out.println("Milleage " + mil);

					}

				}
			}

			// Pricing
			List<PricingInfo> priceInfo = roundTripResults.get(i).getPricing();
			for (int p = 0; p < priceInfo.size(); p++) {
				String price = priceInfo.get(p).getSaleTotal();
				totalPrice += Double.parseDouble(price.substring(3));
				System.out.println("Price " + price);
			}

		}
		System.out.println("__________________________________________________");
		System.out.println("TOTAL PRICE: " + totalPrice);
	}

	private static List<TripOption> getRoundTripResults(int passengersCount, String origin, String dest, String date, int duration,
			int resultCount) {
		List<TripOption> roundTripResults = new ArrayList<TripOption>();
		roundTripResults.addAll(getFlight(passengersCount, origin, dest, date, resultCount));
		roundTripResults.addAll(getFlight(passengersCount,dest,origin,getReturnDate(date,duration),resultCount));
		return roundTripResults;
	}
}