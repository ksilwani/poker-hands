/*
Author: Khalil M. Silwani
Purpose: Programming Exercise
*/
import java.util.*;
public class PokerHands{

  //The instance fields are the characteristics of the hand.
  String player;String suits="";String ranks=""; int[] numRanks; String uniqueRanks; int strength; String description="";

  //Since this program is stand alone, the only constructor is private. The constructor sets a few fields to non-null.
  private PokerHands(){suits = ""; ranks = ""; description = "";}

  //This program uses two PokerHands objects, Black and White.
  private static PokerHands Black,White;

  //Let's user play four rounds of poker.
  public static void main(String[] args){
    Scanner sc = new Scanner(System.in);
    for(int i = 1;i<=4;i++){
	  String hands = sc.nextLine();
	  validate(hands);
	  Black = new PokerHands(); White = new PokerHands();
	  Black.player = "Black"; White.player = "White";
	  setHands(hands);
	  evaluateHands();
  	  PokerHands winner = compareStrengths();
  	  announceWinner(winner);
  	}
  }

  /*validate makes sure players Black and White and their cards are entered correctly.
    If the input does not match the pattern for hands exactly, or if duplicate cards are found, the program ends.*/
  private static void validate(String hands){
	String subPattern = "( [2|3|4|5|6|7|8|9|T|J|Q|K|A][C|D|H|S]){5}";
	String pattern = "Black:" + subPattern + "  White:" + subPattern;
    if(hands.matches(pattern)){
	  boolean distinct = distinguish(hands);
	  if(!distinct){
		System.out.println("Duplicate cards found");System.exit(1);
	   }
    }
	else{
		System.out.println("Invalid hand format:");System.exit(1);
	}
  }

  //Checks for duplicate cards in the input String.
  private static boolean distinguish(String hands){
	java.util.StringTokenizer st = new java.util.StringTokenizer(hands," ");
	while(st.hasMoreTokens()){
	  String s = st.nextToken();
	  boolean duplicateCard = hands.indexOf(s) != hands.lastIndexOf(s);
	  if(duplicateCard){
        return false;
	  }
	}
	return true;
  }

  //Gives meaningful values to the ranks and suits of both players.
  private static void setHands(String hands){
    hands = hands.replace("Black:","").replace("White:","").replace(" ","");
    for(int i=0,j=0;i<10;i+=2,j++){
	  Black.ranks += hands.charAt(i); White.ranks += hands.charAt(i+10);
	  Black.suits += hands.charAt(i+1); White.suits += hands.charAt(i+11);
	}
  }

  //Makes calls which set instance fields necessary to determine the winner.
  private static void evaluateHands(){
	Black.sort();
	White.sort();
    Black.getStrength();
    White.getStrength();
  }

  /*Sets this PokerHands's numRanks and uniqueRanks fields to meaningful values. These fields represent the cards in this object sorted by
    number of occurences and rank, with number of occurences taking priority.*/
  private void sort(){
    numRanks = new int[5];
    uniqueRanks = ""+ranks.charAt(0);
    numRanks[0] = 1;

    for(int i = 1; i<5;i++){
	 int indexC = uniqueRanks.indexOf(ranks.charAt(i));
	 if(indexC == -1){
	   uniqueRanks += ""+ranks.charAt(i);
	   numRanks[uniqueRanks.length()-1] = 1;
	 }
	 else{
       numRanks[indexC]+=1;
     }
   }

   numRanks = Arrays.copyOf(numRanks,uniqueRanks.length());

   String ranksList = "23456789TJQKA";
   char[] uniqueRanksC = uniqueRanks.toCharArray();
   boolean orderChanged = true;
   while(orderChanged){
	 orderChanged = false;
	 for(int i = 0;i<numRanks.length-1;i++){
       if(numRanks[i]<numRanks[i+1]){
		   int tmpi = numRanks[i+1]; numRanks[i+1] = numRanks[i]; numRanks[i] = tmpi;
	   	   char tmpc = uniqueRanksC[i+1]; uniqueRanksC[i+1] = uniqueRanksC[i]; uniqueRanksC[i] = tmpc;
	   	   orderChanged = true;
	   }
	   else if(numRanks[i] == numRanks[i+1]){
         if( ranksList.indexOf(uniqueRanksC[i]) < ranksList.indexOf(uniqueRanksC[i+1]) ){
		   int tmpi = numRanks[i+1]; numRanks[i+1] = numRanks[i]; numRanks[i] = tmpi;
		   char tmpc = uniqueRanksC[i+1]; uniqueRanksC[i+1] = uniqueRanksC[i]; uniqueRanksC[i] = tmpc;
	   	   orderChanged = true;
		 }
	   }
     }
   }
   uniqueRanks = new String(uniqueRanksC);if(uniqueRanks.equals("A5432")){uniqueRanks = "5432A";}
  }

  //Determines this hand's strength, represented as an integer.
  private void getStrength(){
	switch(numRanks[0]){
    case 4: strength = 8;break;
    case 3: strength = numRanks[1]==2 ? 7 : 4;break;
    case 2: strength = numRanks[1]==2 ? 3 : 1;break;
    case 1:
    		boolean straight = "AKQJT98765432".contains(uniqueRanks) || uniqueRanks.equals("5432A");
    		boolean flush = suits.equals("CCCCC") || suits.equals("DDDDD") || suits.equals("HHHHH") || suits.equals("SSSSS");
    		strength = flush && straight ? 9 : flush ? 6 : straight ? 5 : 1;
    		break;
    }
  }

  //Returns a reference to the winning hand, or null if the hands have equal strength and ranks.
  private static PokerHands compareStrengths()
  {
	  if(Black.strength == White.strength){
	  		String ranksList = "23456789TJQKA";
	  		//The loop returns a reference to a hand if a high card plays.
	  		for(int i = 0;i<Black.uniqueRanks.length();i++){
	  		  int blRankVal = ranksList.indexOf(Black.uniqueRanks.charAt(i));
	  		  int whRankVal = ranksList.indexOf(White.uniqueRanks.charAt(i));
	  		  if(blRankVal>whRankVal){
	  		  return Black;
	  		  }
	  		  if(whRankVal>blRankVal){
		      return White;
		      }
	  		}
			return null;//If no high card plays.
	  }
	  else if(Black.strength > White.strength){return Black;}
  	  else{return White;}
  }

  //Assembles information about the winning hand if there is one and displays the result of the round.
  private static void announceWinner(PokerHands winner){
  	String ranksList = "23456789TJQKA";
  	String[] fullRankNames = {"2","3","4","5","6","7","8","9","Ten","Jack","Queen","King","Ace"};

  	if(winner==null){System.out.println("Tie");return;}

	String firstRank = fullRankNames[ranksList.indexOf(winner.uniqueRanks.charAt(0))];
	String secondRank = fullRankNames[ranksList.indexOf(winner.uniqueRanks.charAt(1))];
    switch(winner.strength){
	  case 1: winner.description = "high card: "+firstRank;
	          break;
	  case 2: winner.description = "pair: "+firstRank;
	          break;
	  case 3: winner.description = "two pair: "+firstRank+" and "+secondRank;
	   	      break;
	  case 4: winner.description = "three of a kind: "+firstRank;
	          break;
	  case 5: winner.description = firstRank+" high straight";
	          break;
	  case 6: winner.description = firstRank+" high flush";
	          break;
	  case 7: winner.description = "full house: "+firstRank+" over "+secondRank;
			  break;
	  case 8: winner.description = "four of a kind: "+firstRank;
	          break;
	  case 9: winner.description = firstRank+" high straight flush";
	          break;
	}
	System.out.println(winner.player+" wins. - with "+winner.description);
  }
}