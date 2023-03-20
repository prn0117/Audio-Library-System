public class RemovePlayableCommand implements Command{

    private PlayList aPlayList;
    private Playable aPlayable;
    private int aIndex;


    public RemovePlayableCommand(PlayList pPlayList, int pIndex){
        aPlayList = pPlayList;
        aIndex = pIndex;
    }

    /**
     * @pre aIndex < aPlayList.getList().size() && aIndex > -1
     */
    @Override
    public void execute() {
        assert aIndex >= 0 && aIndex < aPlayList.getList().size();
        aPlayable = aPlayList.getPlayable(aIndex);
        aPlayList.removePlayable(aIndex);

    }

    @Override
    public void undo() {
        aPlayList.addPlayable(aIndex, aPlayable);

    }

    /**
     * @pre aIndex < aPlayList.getList().size() && aIndex > -1
     */
    @Override
    public void redo() {
        assert aIndex >= 0 && aIndex < aPlayList.getList().size();
        aPlayList.removePlayable(aIndex);

    }
}
