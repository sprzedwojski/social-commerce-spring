# Products rated by both users where the ratings differ by less than 2 (i.e. by 0 or 1)
match (m:User {name:"Szymon Przedwojski"})-[r1]-(n:Product)-[r2]-(k:User {name:"Lena Lis"})
where abs(toInt(r1.value)-toInt(r2.value))<2
return m,n,k