package rs.tm.siriusx.snowx.space;

import rs.tm.siriusx.snowx.space.attacker.Attacker;
import rs.tm.siriusx.snowx.space.attacker.impl.HPShip;
import rs.tm.siriusx.snowx.space.attacker.impl.HPTorpedo;
import rs.tm.siriusx.snowx.space.data.Rejectos;
import rs.tm.siriusx.snowx.space.data.Space;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible to detect Rejectos weapons in the space.
 *
 * @author Mohamed Taman
 * @version 1.0
 */
  public final class RejectosDetector {

      private RejectosDetector(){}

      /**
       * This method detects attackers on the space.
       *
       * @param patterns pattern of the attackers.
       * @param space which should be searched for attackers.
       * @return list of all found attackers with full information, about type and X and Y location
       * in the space.
       */
     public static List<Attacker> detectAttackers(List<Rejectos> patterns, Space space) {

        List<Attacker> allAttackers = new ArrayList<>();

        //Loop for all attackers patterns
        for (Rejectos pattern :patterns){

            /*
            Check for the size of pattern dimension is not greater the actual space image
             dimension.
             */
            if (space.getWidth() >= pattern.getWidth() && space.getHeight() >= pattern.getHeight())
            {
                // get the search window of test data
                int endRowIdx,
                        endColIdx,
                        height = space.getHeight() - pattern.getHeight(),
                        width = space.getWidth() - pattern.getWidth();

                for (var startRowIdx = 0; startRowIdx <= height; startRowIdx++)
                {
                    endRowIdx = startRowIdx + pattern.getHeight();

                    for (var startColIdx = 0; startColIdx <= width; startColIdx++)
                    {
                        endColIdx = startColIdx + pattern.getWidth();

                        //Search for attacker in this space window
                        if (isRejectosAttackerFound(pattern, space, startRowIdx, startColIdx,
                                endRowIdx,endColIdx))
                        {
                            allAttackers.add(switch(pattern.getType()){
                                case SHIP -> new HPShip(pattern,startRowIdx,startColIdx);
                                case TORPEDO -> new HPTorpedo(pattern,startRowIdx,startColIdx);
                            });
                        }
                    }
                }
            } else{
                System.err.println("The space dimension is smaller than the Rejectos " +
                        "you trying to detect.");
            }
        }

        return allAttackers;
    }

    private static boolean isRejectosAttackerFound(Rejectos attacker, Space space,
                                                   int startRowIdx,
                                                   int startColIdx, int endRowIdx, int endColIdx)
    {
        var matchedPoints = 0.0f;

        int shapeX = 0, shapeY = 0;

        for (var x = startRowIdx; x < endRowIdx; x++, shapeY = 0, shapeX++)
        {
            for (var y = startColIdx; y < endColIdx; y++, shapeY++)
            {
                /*
                check if + sign for each of space window shape and attacker pattern data at
                the same position then increment the matchedPoints.
                 */
                if (space.getData()[x][y] == '+' && attacker.getData()[shapeX][shapeY] == '+')
                {
                    matchedPoints++;
                }
            }
        }
        //return true if shape has matched points > defined threshold.
        return (matchedPoints / attacker.getTotalPoints() > attacker.getThreshold());
    }
}
