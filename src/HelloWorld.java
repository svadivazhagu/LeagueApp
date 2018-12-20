import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.match.dto.MatchList;
import net.rithms.riot.api.endpoints.match.dto.MatchReference;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Surya on 12/19/2018.
 */
public class HelloWorld {
    public static void main(String[] args) throws RiotApiException  {
        String apiKeyFile = "C:/Users/Surya/Documents/LeagueApp/apiKey.txt";
         FileReader fr = null;
         String apiKey = null;

        File file = new File(apiKeyFile);
        try {
            fr = new FileReader(file);
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
            BufferedReader br = new BufferedReader(fr);
        try {
            apiKey = br.readLine();

        }

        catch (IOException e) {
            System.out.println("IOException");
        }



        ApiConfig config = new ApiConfig().setKey(apiKey);
        RiotApi api = new RiotApi(config);

        //need to get summoner for acc id request 1
        Summoner summoner = api.getSummonerByName(Platform.NA, "ShiftyGoesNifty");

        //use acc id from object summoner to get matchlist data. request 2
        MatchList matchList = api.getMatchListByAccountId(Platform.NA, summoner.getAccountId());

        System.out.println("Number of games to be analyzed = " + matchList.getTotalGames());


        if (matchList.getMatches() != null) {
            ArrayList<String> cleanedMatchRoles = new ArrayList<>();
            for (MatchReference match : matchList.getMatches()) {
                //System.out.println("Game ID: " + match.getGameId());
                if (!"NONE".equals(match.getLane()) ) {
                    cleanedMatchRoles.add(match.getLane());
                }
            }

            for (String role : cleanedMatchRoles) {
                System.out.println(role);
            }
            System.out.println("Your most common role based upon analyzed games is: " + mostCommon(cleanedMatchRoles));



        }

    }
    private static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
    }
}

