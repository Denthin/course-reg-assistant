import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

public class TimeframeImpl implements Timeframe {
    private final String name;
    private final ArrayList<Block> blocks;

    /**
     * Initializes timeframe and, if load, loads timeframe data from named file
     * @param name the name of this timeframe
     */
    public TimeframeImpl(String name, boolean load) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        blocks = new ArrayList<>();

        Path folderPath = Path.of("timeframes");
        Path tfPath = folderPath.resolve(name + "timeframe.txt");
        if (load) {
            if (Files.exists(tfPath)) {
                int minInt = 0;
                BufferedReader reader = new BufferedReader(new FileReader(tfPath.toFile()));
                String line;
                while ((line = reader.readLine()) != null && !line.equals("")) {
                    String[] lineArr = line.split(", ");
                    blocks.add((lineArr.length == 5) ? new RegularBlockImpl(lineArr, minInt) : new BlockImpl(lineArr, minInt));
                    minInt = blocks.get(blocks.size() - 1).getMaxInt() + 1;
                }
            } else {
                throw new FileNotFoundException();
            }
        } else {
            try {
                Files.createFile(tfPath);
            } catch (IOException ignored) {
                //If file already exists, delete contents
                if (Files.exists(tfPath)) {
                    saveTimeframe();
                }
                else {
                    throw new IOException();
                }
            }
        }
    }

    /**
     * Overwrites the existing file with the current blocks in timeframe
     * @throws IOException if the file does not exist or cannot be written to
     */
    private void saveTimeframe() throws IOException {
        Path tfPath = Path.of("timeframes").resolve(name + "timeframe.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(tfPath.toFile()));
        if (blocks.size() > 0) {
            Iterator<Block> iterator = blocks.iterator();
            writer.append(String.join(", ", iterator.next().getInitData()));
            while (iterator.hasNext()) {
                writer.append("\n").append(String.join(", ", iterator.next().getInitData()));
            }
        }
        writer.close();
    }

    /**
     * Appends the last item in blocks() to the timeframe file
     * @throws IOException if the file does not exist or cannot be written to
     */
    private void saveNewBlock() throws IOException {
        try {
            Path tfPath = Path.of("timeframes").resolve(name + "timeframe.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(tfPath.toFile(), true));
            if (blocks.size() > 1) {
                writer.append("\n");
            }
            writer.append(String.join(", ", blocks.get(blocks.size()-1).getInitData()));
            writer.close();
        } catch (IOException e) {
            saveTimeframe();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public String[] getBlockNames() {
        String[] blockNames = new String[blocks.size()];
        for (int i=0; i<blocks.size(); i++) {
            blockNames[i] = blocks.get(i).getName();
        }
        return blockNames;
    }

    @Override
    public String[] getStartTimes(String blockName) {
        for (Block block : blocks) {
            if (block.getName().equals(blockName)) {
                return block.getStartTimes();
            }
        }
        return null;
    }

    @Override
    public String[] getEndTimes(String blockName) {
        for (Block block : blocks) {
            if (block.getName().equals(blockName)) {
                return block.getEndTimes();
            }
        }
        return null;
    }

    @Override
    public int getMaxInt() {
        return (blocks.size() > 0) ? blocks.get(blocks.size() - 1).getMaxInt() : 0;
    }

    @Override
    public boolean isRegular(String blockName) {
        for (Block block : blocks) {
            if (block.getName().equals(blockName)) {
                return block.isRegular();
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public boolean addBlock(String name, String startTime, String endTime) throws IOException {
        for (Block block : blocks) {
            if (block.getName().equals(name)) {
                return false;
            }
        }

        int minInt = (blocks.size() > 0) ? blocks.get(blocks.size()-1).getMaxInt()+1 : 0;
        blocks.add(new BlockImpl(name, startTime, endTime, minInt));
        saveNewBlock();
        return true;
    }

    @Override
    public boolean addBlock(String name, String startTime, String endTime, int classLen, int breakLen) throws IOException {
        for (Block block : blocks) {
            if (block.getName().equals(name)) {
                return false;
            }
        }

        int minInt = (blocks.size() > 0) ? blocks.get(blocks.size()-1).getMaxInt()+1 : 0;
        blocks.add(new RegularBlockImpl(name, startTime, endTime, classLen, breakLen, minInt));
        saveNewBlock();
        return true;
    }

    public float timeToFloat(String time) {
        for (Block block : blocks) {
            if (block.timeToFloat(time) != -1) {
                return block.timeToFloat(time);
            }
        }
        return -1;
    }

    public float timeToFloat(String time, String blockName) {
        for (Block block : blocks) {
            if (block.getName().equals(blockName)) {
                return block.timeToFloat(time);
            }
        }
        return -1;
    }

    public String floatToBlock(float time) {
        if (time < 0) {
            return null;
        }
        for (Block block : blocks) {
            if (time <= block.getMaxInt()) {
                return block.getName();
            }
        }
        return null;
    }

    @Override
    public String floatToTime(float time) {
        for (Block block : blocks) {
            if (time <= block.getMaxInt()) {
                return block.floatToTime(time);
            }
        }
        return null;
    }
}
