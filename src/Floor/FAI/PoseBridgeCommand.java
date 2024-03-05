package Floor.FAI;

import Floor.FTools.NeedPoseBridge;
import mindustry.ai.types.CommandAI;
import mindustry.content.Fx;

public class PoseBridgeCommand extends CommandAI {
    @Override
    public void updateUnit(){
        //assign defaults
        if(command == null && unit.type.commands.length > 0){
            command = unit.type.defaultCommand == null ? unit.type.commands[0] : unit.type.defaultCommand;
        }

        //update command controller based on index.
        var curCommand = command;
        if(lastCommand != curCommand){
            lastCommand = curCommand;
            commandController = (curCommand == null ? null : curCommand.controller.get(unit));
        }

        //use the command controller if it is provided, and bail out.
        if(commandController != null){
            if(commandController.unit() != unit) commandController.unit(unit);

            if(commandController instanceof NeedPoseBridge npb) {
                if(!unit.isPlayer() && targetPos != null){
                    npb.setPose(targetPos);
                }
            }

            commandController.updateUnit();
        }else{
            defaultBehavior();
            //boosting control is not supported, so just don't.
            unit.updateBoosting(false);
        }
    }
}
