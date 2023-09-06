import java.util.Scanner;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;

public class Pig
{
    public static int
    //Default Val: 	Settings:                              
        /*6*/  		dieSidesCount       = 6,   
        /*1*/   	maxEndTurnRollVal   = 1,  
        /*1*/   	playerCount         = 1,   
        /*1*/   	cpuCount            = 1,  
        /*15*/  	cpuBankThreshold    = 15,  
        /*100*/ 	winThreshold        = 100, 
        /*1*/   	podiumPlaces        = 1;    //wip
    public static boolean 
        /*true*/	showFinalStandings  = true,
        /*true*/	showRoundStandings  = true,
        /*true*/	allowCustomNames    = true,
        /*true*/	sortBoardByPoints   = false,
        /*tbd*/  	finishTurnForWinner = false; //wip
			//End of Settings
    private int turnNum,score,roundScore;
    private String playerName;
    private boolean isRolling;
    public static boolean anyWon;
	
    public static void main(String[] args)
    {
		try{
			dieSidesCount = Integer.parseInt(args[0]);
			maxEndTurnRollVal = Integer.parseInt(args[1]);
			playerCount = Integer.parseInt(args[2]);
			cpuCount = Integer.parseInt(args[3]);
			cpuBankThreshold = Integer.parseInt(args[4]);
			winThreshold = Integer.parseInt(args[5]);
			podiumPlaces = Integer.parseInt(args[6]);
			showFinalStandings = Boolean.parseBoolean(args[7]);
			showRoundStandings = Boolean.parseBoolean(args[8]);
			allowCustomNames = Boolean.parseBoolean(args[9]);
			sortBoardByPoints = Boolean.parseBoolean(args[10]);
			finishTurnForWinner = Boolean.parseBoolean(args[11]);
		}
		catch(Exception e){}
		
        System.out.println("Game of Pig\nPress r to roll, b to bank, or s to view scores.");
        ArrayList<Pig> players = new ArrayList<Pig>();
        ArrayList<Pig> cpus = new ArrayList<Pig>();
        ArrayList<Pig> all = new ArrayList<Pig>();
        Scanner nameInput = new Scanner(System.in);
		
        //creates players
        for(int i=1; i<=playerCount; i++)
        {
            if(allowCustomNames){
                System.out.print("Enter Player "+i+" Name: ");
                players.add(new Pig(nameInput.nextLine()));
            }else{
                players.add(new Pig("P"+i));
            }
        }
        //creates bots
        for(int i=1; i<=cpuCount; i++)
        {
            if(cpuCount>1){
                cpus.add(new Pig("CPU"+i));//numbers bots if multiple
            }else{
                cpus.add(new Pig("CPU"));
            }
        }
	//adds all players and bots to a single ArrayList
        all.addAll(players);
        all.addAll(cpus);
		
        //WIP int winners = 0;
        turnRotationLoop: //each loop = 1 complete game turn
        while(!anyWon){
            //Runs through all player turns
            for(Pig p : players){
                p.runPlayerTurn(all);
                if(p.checkWon()){
                    //WIP winners++;
                    //WIP if(!finishTurnForWinner/*&&winners>=podiumPlaces*/){
                        if(showFinalStandings){
                            if(sortBoardByPoints){
                                Collections.sort(all, new SortByPoints());
                                Collections.reverse(all);
                            }
                            System.out.println("Final Scores:");
                            for(Pig a : all){
                                a.finalScoreBoard(all);
                            }
                        }
                        break turnRotationLoop;
                    //}
                }
            }
            //Runs through all bot programs
            for(Pig c : cpus){
                c.runCpuTurn();
                if(c.checkWon()){
                    //winners++;
                    //if(!finishTurnForWinner/*&&winners>=podiumPlaces*/){
                        if(showFinalStandings){
                            if(sortBoardByPoints){
                                Collections.sort(all, new SortByPoints());
                                Collections.reverse(all);
                            }
                            System.out.println("Final Scores:");
                            for(Pig a : all){
                                a.finalScoreBoard(all);
                            }
                        }
                        break turnRotationLoop;
                    //}
                }
            }
            if(showRoundStandings){
				if(sortBoardByPoints){
					Collections.sort(all, new SortByPoints());
					Collections.reverse(all);
				}
                System.out.println("\nRound Complete. \n\nScoreboard:");
                for(Pig a : all){
                    System.out.println(a.playerScoreReport(all));
                }
            }
        }
    }
    //Player constructor
    public Pig(String name){
        turnNum = 1;
        roundScore = 0;
        score = 0;
        playerName = name;
        isRolling = true;
    }
    //executes a roll
    public void roll(){
        System.out.println();
        int diceRoll = (int)(dieSidesCount*Math.random() + 1);
        if(diceRoll>maxEndTurnRollVal){
            roundScore += diceRoll; //adds rolled points and continues turn
            System.out.println(playerName+" rolled a "+diceRoll+"\nThis round "+playerName+" has: "+roundScore);
        }else{
            roundScore = 0;
            turnNum += 1;
            System.out.println(playerName+" rolled a "+diceRoll+"! "+playerName+" gets a zero for this round.");
            System.out.println(playerName+"'s Current Score is: "+score);
            setIsRolling(false);
        }
    }
    //executes a bank
    public void bank(){
        System.out.println();
        score += roundScore;
        roundScore = 0;
        turnNum += 1;
    }
    //simulates a player turn
    public void runPlayerTurn(ArrayList<Pig> all){
        Scanner rb = new Scanner(System.in);
        System.out.println(toString());
        //resets turn ending boolean
        setIsRolling(true);
        while(isRolling){
            String input = rb.nextLine();
            if(input.equals("r")){
                roll();
            }else if(input.equals("b")){
                bank();
                break;
            }else if(input.equals("s")){
                //displays scoreboard on demand
                System.out.println();
                for(Pig a : all){
                    System.out.println(a.playerScoreReport(all));
                }
                System.out.println("\nPress r or b to continue.");
            }else{
                System.out.println("Invalid input! Try again\n");
            }
            if(checkWon()){
                System.out.println(winMessage());
                break;
            }
        }
    }
    //simulates a bot turn
    public void runCpuTurn(){
        System.out.println(toString());
        setIsRolling(true);
        while(isRolling&&roundScore<cpuBankThreshold&&!checkWon()){
            roll();
            if(checkWon()){
                System.out.println(winMessage());
                break;
            }
            if(isRolling&&roundScore>=cpuBankThreshold){
                System.out.print("\n"+playerName+" is banking.");
                bank();
                break;
            }
        }
    }
    //scoreboard following a win
    public void finalScoreBoard(ArrayList<Pig> all){
        System.out.print(playerScoreReport(all));
        if(checkWon()){
            System.out.println("♛");
        }else{
            System.out.println();
        }
    }
    //checks if the conditions for a win have been met
    public boolean checkWon(){
        if(score+roundScore>=winThreshold){
            return true;
        }
        return false;
    }
    public void setIsRolling(boolean rolling){
        isRolling = rolling;
    }
    public boolean getIsRolling(){
        return isRolling;
    }
    public int getPlayerScore(){
        return score;
    }
    public int getRoundScore(){
        return roundScore;
    }
    public int getPlayerTurn(){
        return turnNum;
    }
    public String getPlayerName(){
        return playerName;
    }
    //generates a report of player scores 
    public String playerScoreReport(ArrayList<Pig> all){
        int maxLength = 0;
        for(Pig a : all){
            if(a.getPlayerName().length()>maxLength){
                maxLength = a.getPlayerName().length();
            }
            score+=roundScore;
            roundScore = 0;
        }
        int space = maxLength-getPlayerName().length();
        return getPlayerName()+spacer(space)+" Score is: "+getPlayerScore();
    }
    //generates a victory message 
    public String winMessage(){
        int space = 9-(playerName.length()/2); //centers player name below trophy
        String trophy = 
            "\n   .-=========-.  "+
            "\n   \\'-=======-'/  "+
            "\n   -|   .=.   |-  "+
            "\n  ((|  {{1}}  |)) "+
            "\n   \\|   /|\\   |/  "+
            "\n    \\-_ '`' _-/   "+
            "\n      -`) (`-     "+
            "\n    _/-------\\_   "+
            "\n   /-----------\\  \n";
        return "\n"+playerName+" won in "+turnNum+" turn"+pluralWinTurns()+"!"+trophy+spacer(space)+playerName;
    }
    //adjusts win message in case of single turn victories 
    public String pluralWinTurns(){
        if(turnNum==1){
            return "";
        }else{
            return "s";
        }
    }
    //generates spacing string
    public String spacer(int space){
        String spacer = "";
        for(int i = 0;i<space;i++){
            spacer += " ";
        }
        return spacer;
    }
    //generates string denoting the beginning of a turn
    public String toString(){
        return "\nTurn "+turnNum+"\n"+playerName+"'s Current Score is: "+score+"\nThis round "+playerName+" has: "+roundScore;
    }
}
class SortByPoints implements Comparator<Pig>{
    public int compare(Pig a, Pig b){
        return a.getPlayerScore()-b.getPlayerScore();
    }
}
