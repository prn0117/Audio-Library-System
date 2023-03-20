import java.util.Arrays;
import java.util.LinkedList;

public class ShuffleCommand implements Command{

    private PlayList aPlayList;
    //private PlayList original;
    private LinkedList origList;

    public ShuffleCommand(PlayList pPlayList){
        aPlayList = pPlayList;
    }



    @Override
    public void execute() {
        origList = new LinkedList<>();
        for(Playable p: aPlayList.getList()){
            origList.add(p.clone());
        }
        aPlayList.shuffle();

    }

    @Override
    public void undo() {
        aPlayList.setList(origList);

    }

    @Override
    public void redo() {
        aPlayList.shuffle();

    }
}
