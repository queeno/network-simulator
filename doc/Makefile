NAME = manual

all: full

partial:
	pdflatex $(NAME).tex

full:
	pdflatex $(NAME).tex
	bibtex $(NAME)
	pdflatex $(NAME).tex
	pdflatex $(NAME).tex

clean:
	rm -f *.aux *.log *.err *.out
	rm -f *.toc *.blg *.bbl *.lof

count:
	perl ~/programs/texcount/texcount.pl -inc diss.tex 

view: $(NAME).pdf
	#evince $(NAME).pdf &
	acroread $(NAME).pdf &
