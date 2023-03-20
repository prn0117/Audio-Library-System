public class AddPlayableCommand implements Command{

    private PlayList aPlayList;
    private Playable aPlayable;
    private int aIndex;

    public AddPlayableCommand(PlayList pPlayList, Playable pPlayable){
        aPlayList = pPlayList;
        aPlayable = pPlayable;
    }

    @Override
    public void execute() {

        aPlayList.addPlayable(aPlayable);
    }

    @Override
    public void undo() {
            aPlayList.removePlayable(aPlayList.getList().size()-1);
    }

    @Override
    public void redo() {
        aPlayList.addPlayable(aPlayable);
    }
}