all: tut1 tut2 tut3 tut4

tut%: tut%.vala
	valac --vapidir=. --pkg=xmms2-client -o $@ $<

clean:
	rm -f tut?

