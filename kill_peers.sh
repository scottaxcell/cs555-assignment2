#Use with caution! 
for i in `cat peer_nodes.txt`
do
	ssh $i "killall -u $USER java"
done
