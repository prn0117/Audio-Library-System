import java.util.*;
import java.util.Stack;

/**
 * Represents a sequence of playables to play in FIFO order.
 */
public class PlayList implements Playable {

    private List<Playable> aList = new LinkedList<Playable>();
    private String aName;


    public PlayList(PlayList copy){
        List<Playable> copyList = new LinkedList<Playable>();
        for(Playable p: copy.getList()){
            copyList.add(p.clone());
        }
        this.aList = copyList;
        this.aName = copy.aName;
    }

    public Playable getPlayable(int index){
        assert index>=0 && index < aList.size();
        return aList.get(index);
    }

    public List<Playable> getList(){
        //deep copy using clone
        LinkedList<Playable> copy = new LinkedList<>();
        for(Playable p: aList){
            copy.add(p.clone());
        }
        return copy;

    }

    public void setList(LinkedList<Playable> pList){
        this.aList = pList;
    }

    Stack<Command> undoStack = new Stack<Command>();
    Stack<Command> redoStack = new Stack<Command>();
    Stack<Command> methodStack = new Stack<Command>();

    public void execute(Command pCommand){
        //methodStack.push(pCommand);
        pCommand.execute();
        undoStack.push(pCommand);
        methodStack.push(pCommand);
        redoStack.clear();

        //redoStack.push(pCommand); // added later
    }

    public void undo() {
        if(undoStack.size() > 0) {
            Command pCommand = undoStack.pop();
            pCommand.undo();
            //redoStack.clear();
            redoStack.push(pCommand);
        }
    }

    public void redo() {
        if(redoStack.size() > 0) {
            Command pCommand = redoStack.pop();
            pCommand.redo();
            undoStack.push(pCommand);
        }
        else if(methodStack.size() > 0){
            Command qCommand = methodStack.peek();
            qCommand.redo();
        }
    }
    /**
     * Creates a new empty playlist.
     *
     * @param pName
     *            the name of the list
     * @pre pName!=null;
     */
    public PlayList(String pName) {
        assert pName != null;
        aName = pName;
    }


    public void toStrings() {
        for(Playable p: aList){
            System.out.println(p + " ");
        }
    }

    /**
     * Adds a playable at the end of this playlist.
     *
     * @param pPlayable
     *            the content to add to the list
     * @pre pPlayable!=null;
     */
    public void addPlayable(Playable pPlayable) {
        assert pPlayable != null;
        aList.add(pPlayable);
    }

    public void addPlayable(int index, Playable pPlayable){
        aList.add(index, pPlayable);
    }



    /**
     * remove a playable from the Playlist given its index
     * @param pIndex
     *          the index of playable to be removed
     * @return the removed playable
     */
    public Playable removePlayable(int pIndex) {
        assert pIndex >= 0 && pIndex < aList.size();

        return aList.remove(pIndex);
    }

    /**
     * @return The name of the playlist.
     */
    public String getName() {
        return aName;
    }

    /**
     * modify the name of the playlist
     * @param pName
     *          the new name of the playlist
     */
    public void setName(String pName) {
        //copy before setName and just assign this to the original attribute in order to 'unSetName'
        assert pName != null;
        this.aName = pName;

    }

    /**
     * Iterating through the playlist to play playable content.
     */
    @Override
    public void play() {
        for(Playable playable:aList){
            playable.play();
        }
    }

    @Override
    public PlayList clone() {
        try{
            PlayList clone = (PlayList) super.clone();
            clone.aList = new LinkedList<>();

            // Create deep copy of List
            for(Playable p: aList){
                clone.aList.add(p.clone());// problem here
            }
            return clone;
        }
        catch (CloneNotSupportedException e){
            assert false;
            return null;
        }

    }

    /**
     * change the order how playlist play the playables it contains
     */
    public void shuffle() {
        //copy before shuffling and just assign this to the original list in order to 'unshuffle'
        Collections.shuffle(aList);
    }

    /**
     * Checks is two playlists are equal based on playable objects and their order
     *
     * @param o
     *            The object to compare a playlist to
     * @return    This method returns true if the playlist is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayList playList = (PlayList) o;
        return this.aList.equals(playList.aList);
    }

    /**
     * Equal playlists have the same hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(aList);
    }
}
