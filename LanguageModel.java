import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName)
    {
		String window = "";
        char c = ' ';
        In in = new In (fileName);
       
        
        for (int i=0;i<windowLength;i++)
        {
            c = in.readChar();
            window +=c;

        }

        while (!in.isEmpty())
        {
            c = in.readChar();

            if (CharDataMap.get(window) == null)
            {
                List probs = new List();
                probs.addFirst(c);
                CharDataMap.put(window,probs);
            }
            else
            {
              CharDataMap.get(window).update(c);
            }
            window = window.substring (1,window.length())+c;
		// Your code goes here
        }
        for (List prob : CharDataMap.values())
            {
                calculateProbabilities(prob);
            }
	}

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) 
    {				
        int count = 0;

        for (int i = 0; i < probs.getSize(); i++) 
        {
            count += probs.get(i).count;
        }

        double cumulativeProbability = 0.0;

        for (int i = 0; i < probs.getSize(); i++) 
        {
            CharData charData = probs.get(i);
            double probability = (double) charData.count / count;
            cumulativeProbability += probability;
            charData.p = probability;
            charData.cp = cumulativeProbability;
        }
	}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) 
    {
        double randomR = randomGenerator.nextDouble();
        char ch = ' ';

        for (int i=0; i<probs.getSize();i++)
        {
            if (probs.get(i).cp >= randomR)
            {
                return probs.get(i).chr;
            }
        }

        return ch;
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) 
    {
        if (initialText.length() <= windowLength)
        {
            String newWindow = initialText.substring(initialText.length()-windowLength,initialText.length());
            int i =initialText.length();

            while (initialText.length() != textLength+windowLength)
            {
                if (!CharDataMap.containsKey(newWindow))
                {
                    break;
                }

                List probs = CharDataMap.get(newWindow);
                char nextChar = getRandomChar(probs);
                initialText += nextChar;
                newWindow = newWindow.substring(1) + nextChar;
                i++;

            }

            if (i== textLength)

           { 
            return initialText;
           }
        }

        return initialText;
     
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
    }
}
