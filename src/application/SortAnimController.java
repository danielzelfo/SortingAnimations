package application;

import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class SortAnimController {
	
	//FXML ELEMENTS
	@FXML private TextField n;
	@FXML private ChoiceBox<String> input;
	@FXML private ChoiceBox<String> algorithm;
	@FXML private Pane canvas;
	@FXML private Button runBtn;
	@FXML private Label time;
	@FXML private Slider animSpeed;
	
	//THE MAXIMUM NUMBER OF ELEMENTS
	static private final int MAXN = 1000;
	
	//number of elements (default 100)
	static private int _n = 100;
	
	//the circles (which will be sorted)
	static private Circle c[] = new Circle[MAXN];
	
	//
	static private float diameter = 5;
	static private double maxW;
	static private double maxH;

	//data about the state of the program
	static private boolean generated = false;
	static private int mode = 0;

	//the transitions
	static private SequentialTransition transitions;
	
	//the sorting algorithms
	private final String ALG_INSERT = "Insertion Sort";
	private final String ALG_SELECT = "Selection Sort";
	private final String ALG_SHELL = "Shell Sort";
	private final String ALG_HEAP = "Heap Sort";
	private final String ALG_QUICK = "QuickSort";
	private final String ALG_2QUICK = "2-way QuickSort";
	private final String ALG_QUICK3 = "QuickSort3";

	public void initialize() {
		
		//filling the data choicebox
		input.getItems().add("Random");
		input.getItems().add("Ascending");
		input.getItems().add("Descending");
		input.getSelectionModel().select(0);

		//filling the algorithm choicebox
		algorithm.getItems().add(ALG_INSERT);
		algorithm.getItems().add(ALG_SELECT);
		algorithm.getItems().add(ALG_SHELL);
		algorithm.getItems().add(ALG_HEAP);
		algorithm.getItems().add(ALG_QUICK);
		algorithm.getItems().add(ALG_2QUICK);
		algorithm.getItems().add(ALG_QUICK3);
		algorithm.getSelectionModel().select(0);

		
		//initializing the sequential transitions
		transitions = new SequentialTransition();
		transitions.setOnFinished((e) -> {
			mode = 0;
			runBtn.setText("Run");
		});
		
		
		transitions.rateProperty().bind(animSpeed.valueProperty());

		//setting the canvas size
		maxW = canvas.getWidth();
		maxH = canvas.getHeight();
		
		
		animSpeed.setMin(1);
		animSpeed.setValue(100);
		

	}

	private void generate() {
		int size = Integer.parseInt(n.getText());
		String data = input.getSelectionModel().getSelectedItem().toString();
		
		
		generated = true;
		
		//setting the canvas size
		maxW = canvas.getWidth();
		maxH = canvas.getHeight();

		//validating n
		if (size > MAXN || size < 1)
			return;

		_n = size;

		//removing all the circles
		canvas.getChildren().clear();

		//generating the circles
		for (int i = 0; i < _n; i++) {
			float y = 0.5f;
			if (data == "Random")
				y = (float) Math.random();
			else if (data == "Ascending")
				y = ((float) i) / _n;
			else if (data == "Descending")
				y = (float) (1.0 - ((float) i) / _n);

			c[i] = new Circle(getX(i), getY(y), diameter / 2, Color.BLACK);

			canvas.getChildren().add(c[i]);
		}

	}

	@FXML protected void generateOnClick(ActionEvent event) {
		if(mode == 0)
			generate();
	}

	//start sorting
	private void run() {
		if (!generated)
			generate();
		
		//clearing the transitions
		transitions.getChildren().clear();
		
		
		//running the appropriate sorting algorithm
		String alg = algorithm.getSelectionModel().getSelectedItem().toString();

		long startTime = System.nanoTime();
		if (alg == ALG_INSERT) {
			isort();
		} else if (alg == ALG_SELECT) {
			ssort();
		} else if (alg == ALG_SHELL) {
			shellsort();
		} else if (alg == ALG_HEAP) {
			heapsort();
		} else if (alg == ALG_QUICK) {
			qsort(0, _n - 1);
		} else if (alg == ALG_2QUICK) {
			qsort2(0, _n - 1);
		} else if (alg == ALG_QUICK3) {
			qsort3(0, _n - 1);
		}
		long endTime   = System.nanoTime();
		
		time.setText("  " + Math.round((endTime - startTime)/10000.0)/100.0 + " milliseconds");
		
		mode = 1;

		transitions.play();

		generated = false;
	}

	@FXML protected void runOnClick(ActionEvent event) {

		//sort
		if (mode == 0) {
			runBtn.setText("Pause");
			run();
			
		//pause sorting
		} else if (mode == 1) {
			transitions.pause();
			runBtn.setText("Resume");
			mode = 2;
			
		//resume sorting	
		} else if (mode == 2) {
			transitions.play();
			runBtn.setText("Pause");
			mode = 1;
		}

	}

	//swap the circles given their indexes
	private void baseswap(int i, int j) {

		Circle tempC = c[i];
		c[i] = c[j];
		c[j] = tempC;

	}

	//stretch 0 to _n-1 to width of canvas
	private double getX(int x) { return maxW * (.5 + x) / _n; }
	//stretch 0 to 1 to height of canvas
	private double getY(float y) { return maxH - maxH * y; }

	private void swapAnim(int i, int j) {
		transitions.getChildren().add(new ParallelTransition(moveAnimX(i), moveAnimX(j)));
	}
	private TranslateTransition moveAnimX(int i) {
		TranslateTransition tt = new TranslateTransition(Duration.millis(100), c[i]);
		tt.setToX(getX(i) - c[i].getCenterX());
		return tt;
	}

	private void swap(int i, int j) {
		//swapping the circles in the array
		baseswap(i, j);
		//swap circles in UI
		swapAnim(j, i);
	}

	
	
	//compare circles by their height
	private boolean comp(Circle a, Circle b) {
		return a.getCenterY() > b.getCenterY();
	}

	
	/*
	 * 
	 * The sorting algorithms
	 * 
	 */
	
	private void isort() {

		for (int i = 1; i < _n; i++) {

			for (int j = i; j > 0 && comp(c[j], c[j - 1]); j--) {

				swap(j - 1, j);

			}

		}

	}

	private void ssort() {
		for (int i = 0; i < _n - 1; i++)
			for (int j = i; j < _n; j++)
				if (comp(c[j], c[i]))
					swap(i, j);
	}

	private void shellsort() {
		int i, j, h;
		for (h = 1; h < _n; h = 3 * h + 1)
			;
		for (;;) {
			h /= 3;
			if (h < 1)
				break;
			for (i = h; i < _n; i++) {
				for (j = i; j >= h; j -= h) {
					if (comp(c[j - h], c[j]))
						break;
					swap(j - h, j);
				}
			}
		}
	}

	private void siftdown(int l, int u) {
		int i, c1;
		i = l;
		for (;;) {
			c1 = 2 * i;
			if (c1 > u)
				break;
			if (c1 + 1 <= u && comp(c[c1], c[c1 + 1]))
				c1++;
			if (!comp(c[i], c[c1]))
				break;
			swap(i, c1);
			i = c1;
		}
	}

	private void heapsort()
	{
		int i;
		for (i = _n / 2 - 1; i >= 0; i--)
			siftdown(i, _n - 1);
		for (i = _n - 1; i >= 1; i--) {
			swap(0, i);
			siftdown(0, i - 1);
		}
	}

	private void qsort(int l, int u) {
		if (l >= u)
			return;
		int m = l;
		for (int i = l + 1; i <= u; i++)
			if (comp(c[i], c[l]))
				swap(++m, i);
		swap(l, m);
		qsort(l, m - 1);
		qsort(m + 1, u);
	}

	void qsort2(int l, int u) {
		if (l >= u)
			return;
		int i = l;
		int j = u + 1;
		for (;;) {
			do
				i++;
			while (i <= u && comp(c[i], c[l]));
			do
				j--;
			while (comp(c[l], c[j]));
			if (i > j)
				break;
			swap(i, j);
		}
		swap(l, j);
		qsort2(l, j - 1);
		qsort2(j + 1, u);
	}

	
	//the median of 3 quicksort algorithm
	private void qsort3(int l, int u) {
		if (l >= u)
			return;

		
		//swapping the median of the first, middle, and last value with the first value
		if ((comp(c[l], c[(u - l) / 2]) && comp(c[(u - l) / 2], c[u])) || (comp(c[u], c[(u - l) / 2]) && comp(c[(u - l) / 2], c[l]))) 
			swap((u - l) / 2, l);
		else if (!((comp(c[(u - l) / 2], c[l]) && comp(c[l], c[u])) || (comp(c[u], c[l]) && comp(c[l], c[(u - l) / 2])))) 
			swap(u, l);
			

		int m = l;

		for (int i = l + 1; i <= u; i++)
			if (comp(c[i], c[l]))
				swap(++m, i);
		swap(l, m);
		qsort(l, m - 1);
		qsort(m + 1, u);
	}
}
