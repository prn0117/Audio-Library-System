public class SetNameCommand implements Command{

    private PlayList aPlayList;
    private String aName;
    private String original;
    //private PlayList original;

    public SetNameCommand(PlayList pPlayList, String pName){
        aPlayList = pPlayList;
        original = pPlayList.getName();
        aName = pName;
        //original = pPlayList.clone(); // make deep copy
    }

    @Override
    public void execute() {
        aPlayList.setName(aName);

    }

    @Override
    public void undo() {
        //aPlayList = original;
        aPlayList.setName(original);

    }

    @Override
    public void redo() {
        aPlayList.setName(aName);

    }
}