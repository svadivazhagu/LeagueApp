import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.champion.dto.Champion;
import net.rithms.riot.api.endpoints.match.dto.MatchList;
import net.rithms.riot.api.endpoints.match.dto.MatchReference;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.io.*;
import java.util.*;


/**
 * Created by Surya on 12/19/2018.
 */
public class HelloWorld {
    public static void main(String[] args) throws RiotApiException  {
        AutobotsAssemble();
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
    //Method for obtaining the person's most common role based upon recent games.
    private static String RoleFinder(MatchList matchList) {

        if (matchList.getMatches() != null) {
            ArrayList<String> cleanedMatchRoles = new ArrayList<>();
            for (MatchReference match : matchList.getMatches()) {
                //System.out.println("Game ID: " + match.getGameId());
                if (!"NONE".equals(match.getLane()) ) {
                    cleanedMatchRoles.add(match.getLane());
                }
            }

//            for (String role : cleanedMatchRoles) {
//                System.out.println(role);
//            }
            return ("Your most common role based upon analyzed games is: " + mostCommon(cleanedMatchRoles));
        }
        return "Matches are null.";
    }
    //method loading in the api key from file
    private static String ApiKeyLoader(String apiKeyPath) {
        String apiKeyFile = apiKeyPath ;
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

        return apiKey;
    }
    //asks user what option they want & what user they want to ask for., calls method accordingly. if they want to use additional features, it will reuse the same summoner name.
    private static void AutobotsAssemble() throws RiotApiException {
        Scanner filepath = new Scanner(System.in);
        System.out.println("Welcome to SV's LoLApp. Enter Riot API Key Local Filepath.");
        String fp = filepath.nextLine();
        if (fp.isEmpty()) {
            System.out.println("You didn't specify a filepath, SV default path used.");
            fp = "C:/Users/Surya/Documents/LeagueApp/apiKey.txt";
        }

        Scanner summonerName = new Scanner(System.in);
        System.out.println("Please enter desired Summoner Name for analysis.");
        String summ = summonerName.nextLine();
        if (summ.isEmpty()) {
            System.out.println("You didn't specify a Summoner Name, SV default name used.");
            summ = "ShiftyGoesNifty";
        }
        else{
            System.out.println("The name " + summ + " will be analyzed.");
        }

        ApiConfig config = new ApiConfig().setKey(ApiKeyLoader(fp));
        RiotApi api = new RiotApi(config);

        //need to get summoner from user input.
        Summoner summoner = api.getSummonerByName(Platform.NA, summ);

        //use acc id from object summoner to get matchlist data. request 2
        MatchList matchList = api.getMatchListByAccountId(Platform.NA, summoner.getAccountId());

        Scanner choiceScanner = new Scanner(System.in);
        System.out.println("Which functionality of the LolApp are you interested in using? Enter the corresponding number" +
                "inside the bracket for your preferred option.");
        System.out.println("[0] Most Common Role");
        System.out.println("[1] Average Times for Recent Games");
        System.out.println("[2] Most Common Champion Played");
        String opt = choiceScanner.nextLine();

        //switch for function choosing
        switch (opt) {
            case "0":
                System.out.println(RoleFinder(matchList));
            case "1":
                System.out.println(AvgTimestamp(matchList));
            case "2":
                System.out.println(MostCommonChamp(matchList, api));

        }
    }

    private static String AvgTimestamp(MatchList  matchList) {
        if (matchList.getMatches() != null) {
            ArrayList<Long> cleanedMatchTimes = new ArrayList<>();
            ArrayList<Integer> convertedMatchTimes = new ArrayList<>();
            for (MatchReference match : matchList.getMatches()) {
                //System.out.println("Game ID: " + match.getGameId());
                if (!"NONE".equals(match.getTimestamp()) ) {
                    cleanedMatchTimes.add((match.getTimestamp()));
                }
            }

            Double avg = 0.0;
            for (long i : cleanedMatchTimes) {
                Date date  = new Date(i);
                int seconds = (int) (i / 1000);
                int minutes = seconds %60;
                convertedMatchTimes.add(minutes);


                avg += i;
            }

            avg = ((((avg / convertedMatchTimes.size()) / 1000) / 60));

            return ("Average gametime is " + avg);
        }
        return "Matches are null.";
    }

    private static String MostCommonChamp(MatchList matchList, RiotApi api) throws RiotApiException {
        if (matchList.getMatches() != null) {
            ArrayList<Integer> cleanedChampions= new ArrayList<>();
            ArrayList<String> postConversion = new ArrayList<>();
            for (MatchReference match : matchList.getMatches()) {
                //System.out.println("Game ID: " + match.getGameId());
                if (!"NONE".equals(match.getChampion()) ) {
                    cleanedChampions.add((match.getChampion()));
                }
            }

            for (int champion : cleanedChampions) {
                postConversion.add((api.getDataChampion(Platform.NA, champion)).getName());
            }

            return mostCommon(postConversion);
        }
        return "Matches are null.";
    }
}

