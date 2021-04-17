package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.*;

public class Main {

    public static TreeMap<String, Integer> errors = new TreeMap<>();
    public static ArrayList<String> errorKey = new ArrayList<>();
    public static LinkedHashMap<String, String> cardMap = new LinkedHashMap<>();
    public static ArrayList<String> logs = new ArrayList<>();

    public static void main(String[] args){
        boolean loop = true;
        String impFile = null;
        String expFile = null;
        for(int i = 0 ; i< args.length-1; i++){
            if(args[i].equals("-import")){
                impFile = args[i+1];
            }else if(args[i].equals("-export")){
                expFile= args[i+1];
            }
        }
        if(impFile!= null){
            importFile(impFile);
        }

        while(loop){
            showOutput("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats): ");
            String action = getInput();
            switch(action){
                case "add":
                    addCard();
                    break;
                case "remove":
                    removeCard();
                    break;
                case "import":
                    importFile();
                    break;
                case "export":
                    exportFile();
                    break;
                case "ask":
                    askCard();
                    break;
                case "log":
                    log();
                    break;
                case "hardest card":
                    hardestCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                case "exit":
                    showOutput("Bye,  Bye! ");
                    if(expFile != null){
                        exportFile(expFile);
                    }
                    loop = false;
                    break;
                default:
                    loop = false;
                    break;
            }

        }
    }

    public static void addCard(){
        showOutput("The card: ");
        String term = getInput();
        if(cardMap.containsKey(term)){
            showOutput("The card \"" + term + "\" already exists.");
        }else{
            showOutput("The definition of the card: ");
            String definition = getInput();
            if(cardMap.containsValue(definition)){
                showOutput("The definition \"" + definition + "\" already exists.");
            }else{
                cardMap.put(term, definition);
                showOutput("The pair (\"" + term +"\":\"" + definition + "\") has been added.");
            }
        }
    }

    public static void removeCard(){
        showOutput("Which card?");
        String card = getInput();
        if(cardMap.containsKey(card)){
            cardMap.remove(card);
            errors.remove(card);
            showOutput("The card has been removed. ");
        }else{
            showOutput("Can't remove \"" + card + "\": there is no such card.");
        }
    }

    public static void importFile(){
        showOutput("File name: ");
        String fileName = getInput();
        File file = new File(fileName);
        int count = 0;
        try(Scanner scan = new Scanner(file)){
            while(scan.hasNextLine()){
                String term = scan.nextLine();
                String definition = scan.nextLine();
                cardMap.put(term, definition);
                count++;
            }
        }catch(FileNotFoundException e){
            showOutput("File not found! ");
        }

        File errorFile = new File("errors.txt");
        try(Scanner scan = new Scanner(errorFile)){
            while(scan.hasNextLine()){
                String term = scan.nextLine();
                int error = Integer.parseInt(scan.nextLine());
                errors.put(term, error);
            }
        }catch(FileNotFoundException e){
            showOutput("Error file not found! ");
        }

        showOutput(count + " cards have been loaded.");
    }

    public static void importFile(String filePath){
        File file = new File(filePath);
        int count = 0;
        try(Scanner scan = new Scanner(file)){
            while(scan.hasNextLine()){
                String term = scan.nextLine();
                String definition = scan.nextLine();
                cardMap.put(term, definition);
                count++;
            }
        }catch(FileNotFoundException e){
            showOutput("File not found! ");
        }

        File errorFile = new File("errors.txt");
        try(Scanner scan = new Scanner(errorFile)){
            while(scan.hasNextLine()){
                String term = scan.nextLine();
                int error = Integer.parseInt(scan.nextLine());
                errors.put(term, error);
            }
        }catch(FileNotFoundException e){
            showOutput("Error file not found! ");
        }

        showOutput(count + " cards have been loaded.");
    }

    public static void exportFile(){
        showOutput("File name: ");
        String fileName = getInput();
        File file = new File(fileName);
        int count = 0;
        try(FileWriter fw = new FileWriter(file)){
            for(String s: cardMap.keySet()){
                fw.write(s + "\n" + cardMap.get(s) + "\n");
                count++;
            }
            fw.flush();
        }catch(Exception e){
            showOutput("Error");
            e.printStackTrace();
        }

        File errorFile = new File("errors.txt");
        try(FileWriter fw = new FileWriter(errorFile)){
            for(var v: errors.entrySet()){
                fw.write(v.getKey() + "\n" + v.getValue() + "\n");
            }
            fw.flush();
        }catch(Exception e){
            showOutput("Error during export! ");
            e.printStackTrace();
        }
        showOutput(count + " cards have been saved.");
    }

    public static void exportFile(String filePath){
        File file = new File(filePath);
        int count = 0;
        try(FileWriter fw = new FileWriter(file)){
            for(String s: cardMap.keySet()){
                fw.write(s + "\n" + cardMap.get(s) + "\n");
                count++;
            }
            fw.flush();
        }catch(Exception e){
            showOutput("Error");
            e.printStackTrace();
        }

        File errorFile = new File("errors.txt");
        try(FileWriter fw = new FileWriter(errorFile)){
            for(var v: errors.entrySet()){
                fw.write(v.getKey() + "\n" + v.getValue() + "\n");
            }
            fw.flush();
        }catch(Exception e){
            showOutput("Error during export! ");
            e.printStackTrace();
        }
        showOutput(count + " cards have been saved.");
    }

    public static void askCard(){
        showOutput("How many times to ask?");
        int n = Integer.parseInt(getInput());
        int count = 0;
        for(String s: cardMap.keySet()){
            if(n == count){
                break;
            }
            showOutput("Print the definition of \"" + s + "\":");
            String answer = getInput();
            if(cardMap.get(s).equals(answer)){
                showOutput("Correct!");
            }else{
                newError(s);
                String out = "Wrong. The right answer is \"" + cardMap.get(s) + "\"";
                for(String val : cardMap.values()){
                    if(answer.equals(val)){
                        out += ", but your definition is correct for \"" + getKeyByValue(cardMap, val) + "\"";
                    }
                }
                out += ".";
                showOutput(out);
            }
            count++;
        }
    }

    public static void log(){
        showOutput("File name:");
        String name = getInput();
        File file = new File(name);
        try(FileWriter fw = new FileWriter(file)){
            for(String s: logs){
                fw.write(s + "\n");
            }
            fw.write("The log has been saved. \n Another line!");
            showOutput("The log has been saved. ");
            fw.flush();
        }catch (Exception e){
            showOutput("File not found");
            e.printStackTrace();
        }
    }

    public static void hardestCard(){
        if(errors.isEmpty()){
            showOutput("There are no cards with errors.");
        }else{
            int initialValue = 0;
            for(var v: errors.entrySet()){
                String key = v.getKey();
                if(errors.get(key) > initialValue){
                    //errorKey.clear();
                    initialValue = errors.get(key);
                    errorKey.add(key);
                }else if(errors.get(key) == initialValue){
                    errorKey.add(key);
                }
            }
            if(errorKey.size() == 1){
                showOutput("The hardest card is \"" + errorKey.get(0) + "\". You have "+ errors.get(errorKey.get(0)) + " errors answering it.");
            }else{
                String out = "The hardest cards are ";
                for(int i = 0; i < errorKey.size(); i++){
                    if(i == errorKey.size()-1){
                        out+= "\"" + errorKey.get(i) + "\".";
                    }else{
                        out += "\"" + errorKey.get(i) + "\", ";
                    }
                }
                showOutput(out + " You have " + errors.get(errorKey.get(0)) + " errors answering them. ");
            }
        }
    }

    public static void showOutput(String s){
        System.out.println(s);
        logs.add(s);
    }

    public static String getInput(){
        Scanner s = new Scanner(System.in);
        String str = s.nextLine().trim();
        logs.add(str);
        return str;
    }

    public static void resetStats(){
        errors.clear();
        showOutput("Card statistics have been reset.");
    }

    public static void newError(String key){
        if(errors.containsKey(key)){
            errors.put(key, errors.get(key) + 1);
        }else{
            errors.put(key, 1);
        }
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
