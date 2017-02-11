

library(ggplot2);
# Multiple plot function
#
# ggplot objects can be passed in ..., or to plotlist (as a list of ggplot objects)
# - cols:   Number of columns in layout
# - layout: A matrix specifying the layout. If present, 'cols' is ignored.
#
# If the layout is something like matrix(c(1,2,3,3), nrow=2, byrow=TRUE),
# then plot 1 will go in the upper left, 2 will go in the upper right, and
# 3 will go all the way across the bottom.
#
multiplot <- function(..., plotlist=NULL, file, cols=1, layout=NULL) {
  library(grid)
  
  # Make a list from the ... arguments and plotlist
  plots <- c(list(...), plotlist)
  
  numPlots = length(plots)
  
  # If layout is NULL, then use 'cols' to determine layout
  if (is.null(layout)) {
    # Make the panel
    # ncol: Number of columns of plots
    # nrow: Number of rows needed, calculated from # of cols
    layout <- matrix(seq(1, cols * ceiling(numPlots/cols)),
                     ncol = cols, nrow = ceiling(numPlots/cols))
  }
  
  if (numPlots==1) {
    print(plots[[1]])
    
  } else {
    # Set up the page
    grid.newpage()
    pushViewport(viewport(layout = grid.layout(nrow(layout), ncol(layout))))
    
    # Make each plot, in the correct location
    for (i in 1:numPlots) {
      # Get the i,j matrix positions of the regions that contain this subplot
      matchidx <- as.data.frame(which(layout == i, arr.ind = TRUE))
      
      print(plots[[i]], vp = viewport(layout.pos.row = matchidx$row,
                                      layout.pos.col = matchidx$col))
    }
  }
}

df <- read.csv("res1.csv",header=T);

plot1 <- ggplot(data=df, aes(x=Taille, y=ex))+ 
  geom_point(alpha=.1) +
  geom_smooth(alpha=.2, size=1) +
  ggtitle("Nombre d'appels en fonction de la taille (100000 itérations)") +
  ylab("Nombre d'appels") +
  xlab("Taille du message") +ylim(0,80);
plot1;

df2 <- read.csv("res2.csv",header=T);
plot2 <- ggplot(data=d2, aes(x=Taille, y=ex))+ 
  geom_point(alpha=.1) +
  geom_smooth(alpha=.2, size=1) +
  ggtitle("Nombre d'appels en fonction de la taille (500000 itérations)") +
  ylab("Nombre d'appels") +
  xlab("Taille du message")+ylim(0,80);
plot2;

multiplot(plot1,plot2, cols=1);
