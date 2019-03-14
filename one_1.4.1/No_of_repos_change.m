% Make a function out of this, that takes the reports folder address and
% the number of runs as variables;

clear
M1 = dlmread('reports3/MDMR1', ' ', 0, 2);
S1 = dlmread('reports3/RAMR1', ' ', 0, 2);
PB1 = dlmread('reports3/RPBWR1', ' ', 0, 2);
UB1 = dlmread('reports3/RUPBWR1', ' ', 0, 2);
SB1 = dlmread('reports3/RSBWR1', ' ', 0, 2);
RI1 = dlmread('reports3/RISR1', ' ', 0, 2);
RP1 = dlmread('reports3/RPrMR1', ' ', 0, 2);
RS1 = dlmread('reports3/RSMR1', ' ', 0, 2);

M2 = dlmread('reports3/MDMR2', ' ', 0, 2);
S2 = dlmread('reports3/RAMR2', ' ', 0, 2);
UB2 = dlmread('reports3/RUPBWR2', ' ', 0, 2);
PB2 = dlmread('reports3/RPBWR2', ' ', 0, 2);
SB2 = dlmread('reports3/RSBWR2', ' ', 0, 2);
RI2 = dlmread('reports3/RISR2', ' ', 0, 2);
RP2 = dlmread('reports3/RPrMR2', ' ', 0, 2);
RS2 = dlmread('reports3/RSMR2', ' ', 0, 2);

M3 = dlmread('reports3/MDMR3', ' ', 0, 2);
S3 = dlmread('reports3/RAMR3', ' ', 0, 2);
PB3 = dlmread('reports3/RPBWR3', ' ', 0, 2);
UB3 = dlmread('reports3/RUPBWR3', ' ', 0, 2);
SB3 = dlmread('reports3/RSBWR3', ' ', 0, 2);
RI3 = dlmread('reports3/RISR3', ' ', 0, 2);
RP3 = dlmread('reports3/RPrMR3', ' ', 0, 2);
RS3 = dlmread('reports3/RSMR3', ' ', 0, 2);

M4 = dlmread('reports3/MDMR4', ' ', 0, 2);
S4 = dlmread('reports3/RAMR4', ' ', 0, 2);
PB4 = dlmread('reports3/RPBWR4', ' ', 0, 2);
UB4 = dlmread('reports3/RUPBWR4', ' ', 0, 2);
SB4 = dlmread('reports3/RSBWR4', ' ', 0, 2);
RI4 = dlmread('reports3/RISR4', ' ', 0, 2);
RP4 = dlmread('reports3/RPrMR4', ' ', 0, 2);
RS4 = dlmread('reports3/RSMR4', ' ', 0, 2);

% M5 = dlmread('reports3/MDMR5', ' ', 0, 2);
% S5 = dlmread('reports3/RAMR5', ' ', 0, 2);
% UB5 = dlmread('reports3/RUPBWR5', ' ', 0, 2);
% PB5 = dlmread('reports3/RPBWR5', ' ', 0, 2);
% SB5 = dlmread('reports3/RSBWR5', ' ', 0, 2);
% RI5 = dlmread('reports3/RISR5', ' ', 0, 2);
% RP5 = dlmread('reports3/RPrMR5', ' ', 0, 2);
% RS5 = dlmread('reports3/RSMR5', ' ', 0, 2);

% M6 = dlmread('reports3/MDMR6', ' ', 0, 2);
% S6 = dlmread('reports3/RAMR6', ' ', 0, 2);
% PB6 = dlmread('reports3/RPBWR6', ' ', 0, 2);
% UB6 = dlmread('reports3/RUPBWR6', ' ', 0, 2);
% SB6 = dlmread('reports3/RSBWR6', ' ', 0, 2);
% RI6 = dlmread('reports3/RISR6', ' ', 0, 2);
% RP6 = dlmread('reports3/RPrMR6', ' ', 0, 2);
% RS6 = dlmread('reports3/RSMR6', ' ', 0, 2);

% R6 = dlmread('proc_sims/RDMR6', ' ', 0, 1);
% M6 = dlmread('proc_sims/MDMR6', ' ', 0, 1);
% S6 = dlmread('proc_sims/RSMLR6', ' ', 0, 2);
[r21, c21] = size(M1);
[r22, c22] = size(M2);
[r31, c31] = size(S1);
[r32, c32] = size(S2);
maxstorM1 = zeros(c21, 0);
maxstor = zeros(c31, 0);

% for i = 1:r11
%     maxstorR1(i) = max(R1(i, :));
% end
% maxval1 = max(maxstorR1);

for i = 1:r21
    maxstorM1(i) = max(M1(i, :));
end
maxval2 = max(maxstorM1);

% for inc = 5
%     if inc == 1
%         S = S1;
%     end
%     
%     if inc == 2
%         S = S2;
%     end
%     
%     if inc == 3
%         S = S3;
%     end
%     
%     if inc == 4
%         S = S4;
%     end
%     
%     if inc == 5
%         S = S5;
%     end
%     
% %     if inc == 6
% %         S = S6;
% %     end
% %     
% %     if inc == 7
% %         S = S7;
% %     end
% %     
% %     if inc == 8
% %         S = S8;
% %     end
% %     
% %     if inc == 9
% %         S = S9;
% %     end
% %     
% %     if inc == 10
% %         S = S10;
% %     end
% %     
% %     if inc == 11
% %         S = S11;
% %     end
% %     
% %     if inc == 12
% %         S = S12;
% %     end
% %     
% %     if inc == 13
% %         S = S13;
% %     end
% %     
% %     if inc == 14
% %         S = S14;
% %     end
% %     
% %     if inc == 15
% %         S = S15;
% %     end
%     
%     s = 10000;
%     col = 1;
%     for i = 1:r31
%         maxstor(i) = max(S(i, :));
%         if maxstor(i) >= 15000 && i < s
%             c0 = 1;
%             c50 = 1;
%             for c = 1:c31
%                 if S(i, c) >= 7500
%                     fillperc50_100(c50) = S(i, c)/15000*100;
%                     c50=c50+1;
%                 else
%                     fillperc0_50(c0) = S(i, c)/15000*100;
%                     c0=c0+1;
%                 end
%             end
%             figure
%             bar(S(i, :));
%             s = i; 
%             %s = 1434 for 1000 cars
%             %s = 1266 for 40 cars
%         end
% 
%         c100 = 1;
%         for c = 1:c31
%             if S(i, c) >= 14000
%                 fill100(c100) = 1;
%             else
%                 fill100(c100) = 0;
%             end
%             c100=c100+1;
%         end
%         filled100(i, :) = fill100;        
%         fillperc100(col) = sum(fill100)/40*100;
% 
%         c50 = 1;
%         for c = 1:c31
%             if S(i, c) >= 7500
%                 fill50(c50) = 1;
%             else
%                 fill50(c50) = 0;
%             end
%             c50=c50+1;
%         end
%         filled50(i, :) = fill50;
%         fillperc50(col) = sum(fill50)/40*100;
% 
%         c25 = 1;
%         for c = 1:c31
%             if S(i, c) >= 4507
%                 fill25(c25) = 1;
%             else
%                 fill25(c25) = 0;
%             end
%             c25=c25+1;
%         end
%         filled25(i, :) = fill25;
%         fillperc25(col) = (sum(fill25))/40*100;
%         col = col + 1;
%     end
% 
%     maxval3 = max(maxstor);
%     
%     if inc == 1
%         c3 = c31;
%         r3 = r31;
%         maxstorS1 = maxstor;
%         fillpercS1 = [fillperc25(:); fillperc50(:); fillperc100(:)];
%         filled100S1 = filled100;
%         filled50S1 = filled50;
%         filled25S1 = filled25;
%         for repo = 1:c3
%             reposfillS1(repo) = mean(S(:, repo));
%             reposfill(repo, inc) = reposfillS1(repo);           
%         end
%         fillpercerrbarS1 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
%                             mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
%                             mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
%     end
%     
%     if inc == 2
%         c3 = c32;
%         r3 = r32;
%         maxstorS2 = maxstor;
%         fillpercS2 = [fillperc25(:); fillperc50(:); fillperc100(:)];
%         for repo = 1:c3
%             reposfillS2(repo) = mean(S(:, repo));
%             reposfill(repo, inc) = reposfillS2(repo);           
%         end
%         fillpercerrbarS2 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
%                             mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
%                             mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
%     end
%     
%     if inc == 3
%         c3 = c31;
%         r3 = r31;
%         maxstorS3 = maxstor;
%         fillpercS3 = [fillperc25(:); fillperc50(:); fillperc100(:)];
%         for repo = 1:c3
%             reposfillS3(repo) = mean(S(:, repo));
%             reposfill(repo, inc) = reposfillS3(repo);           
%         end
%         fillpercerrbarS3 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
%                             mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
%                             mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
%     end
%     
%     if inc == 4
%         c3 = c31;
%         r3 = r31;
%         maxstorS1 = maxstor;
%         fillpercS4 = [fillperc25(:); fillperc50(:); fillperc100(:)];
%         for repo = 1:c3
%             reposfillS4(repo) = mean(S(:, repo));
%             reposfill(repo, inc) = reposfillS4(repo);           
%         end
%         fillpercerrbarS4 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
%                             mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
%                             mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
%     end
%     
%     if inc == 5
%         c3 = c31;
%         r3 = r31;
%         maxstorS5 = maxstor;
%         fillpercS5 = [fillperc25(:); fillperc50(:); fillperc100(:)];
%         for repo = 1:c3
%             reposfillS5(repo) = mean(S(:, repo));
%             reposfill(repo, inc) = reposfillS5(repo);
%         end
%         fillpercerrbarS5 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
%                             mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
%                             mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
%     end
%     
%     if inc == 6
%         c3 = c31;
%         r3 = r31;
%         maxstorS6 = maxstor;
%         fillpercS6 = [fillperc25(:); fillperc50(:); fillperc100(:)];
%         for repo = 1:c3
%             reposfillS6(repo) = mean(S(:, repo));
%             reposfill(repo, inc) = reposfillS6(repo);
%         end
%         fillpercerrbarS6 = [mode(fillperc25(:)), mean(fillperc25(:)), max(fillperc25(:)); ...
%                             mode(fillperc50(:)), mean(fillperc50(:)), max(fillperc50(:)); ...
%                             mode(fillperc100(:)), mean(fillperc100(:)), max(fillperc100(:))];
%     end
% %     
% %     for repo = 1:c3
% %         
% %     end
%                 
% end


% deleted_29 = [max(R1(:, 29)), max(R2(:, 29)), max(R3(:, 29)), max(R4(:, 29))];
% 
% figure
% plot([1 2 3 4], deleted_29);

for repo = 1:40
    upspeeds1(repo, :) = [mean(SB1(1:10000, repo)), mean(UB1(1:10000, repo)), mean(PB1(1:10000, repo))];
    inspeeds1(repo, :) = mean(RI1(1:10000, repo));
end

for repo = 1:80
    upspeeds2(repo, :) = [mean(SB2(1:10000, repo)), mean(UB2(1:10000, repo)), mean(PB2(1:10000, repo))];
    inspeeds2(repo, :) = mean(RI2(1:10000, repo));
end

for repo = 1:100
    upspeeds3(repo, :) = [mean(SB3(1:10000, repo)), mean(UB3(1:10000, repo)), mean(PB3(1:10000, repo))];
    inspeeds3(repo, :) = mean(RI3(1:10000, repo));
end

for repo = 1:120
    upspeeds4(repo, :) = [mean(SB4(1:10000, repo)), mean(UB4(1:10000, repo)), mean(PB4(1:10000, repo))];
    inspeeds4(repo, :) = mean(RI4(1:10000, repo));
end

mdeleted1 = [mean(M1(10000, 1:40)), mean(M1(10000, 41:840)), mean(M1(10000, 541:855));
             mean(M2(10000, 1:40)), mean(M2(10000, 41:840)), mean(M2(10000, 541:855));
             mean(M3(10000, 1:40)), mean(M3(10000, 41:840)), mean(M3(10000, 541:855));
             mean(M4(10000, 1:40)), mean(M4(10000, 41:840)), mean(M4(10000, 541:855))];
for i = 1:10000
    RS1_mean(i) = mean(RS1(i, :));
    RP1_mean(i) = mean(RP1(i, :));
    RS2_mean(i) = mean(RS2(i, :));
    RP2_mean(i) = mean(RP2(i, :));
    RS3_mean(i) = mean(RS3(i, :));
    RP3_mean(i) = mean(RP3(i, :));
    RS4_mean(i) = mean(RS4(i, :));
    RP4_mean(i) = mean(RP4(i, :));
end
storages_30 = [mean(RS1_mean), mean(RP1_mean);
                mean(RS2_mean), mean(RP2_mean); 
                mean(RS3_mean), mean(RP3_mean);
                mean(RS4_mean), mean(RP4_mean)];

for i = 1:10000
    RI1_mean(i) = mean(RI1(i, :));
    RI2_mean(i) = mean(RI2(i, :));
    RI3_mean(i) = mean(RI3(i, :));
    RI4_mean(i) = mean(RI4(i, :));
end
inspeeds1_30 = [mean(RI1_mean), mean(RI2_mean), mean(RI3_mean), mean(RI4_mean)];

for i = 1:10000
    SB1_mean(i) = mean(SB1(i, :));
    UB1_mean(i) = mean(UB1(i, :));
    PB1_mean(i) = mean(PB1(i, :));
    SB2_mean(i) = mean(SB2(i, :));
    UB2_mean(i) = mean(UB2(i, :));
    PB2_mean(i) = mean(PB2(i, :));
    SB3_mean(i) = mean(SB3(i, :));
    UB3_mean(i) = mean(UB3(i, :));
    PB3_mean(i) = mean(PB3(i, :));
    SB4_mean(i) = mean(SB4(i, :));
    UB4_mean(i) = mean(UB4(i, :));
    PB4_mean(i) = mean(PB4(i, :));
end
    
upspeeds1_30 = [mean(SB1_mean), mean(UB1_mean), mean(PB1_mean);
                mean(SB2_mean), mean(UB2_mean), mean(PB2_mean); 
                mean(SB3_mean), mean(UB3_mean), mean(PB3_mean);
                mean(SB4_mean), mean(UB4_mean), mean(PB4_mean)];
% mdeleted2 = M2(10000, :);
% mdeleted3 = M3(10000, :);
% addr1 = 80:334;
% addr2 = 80:634;
% addr3 = 80:1134;


% Most important assessment in the simulations...maybe could create others
% in the same way...? Also, try with plot instead...
% Maybe try and integrate (average) storage with this, as well?
figure
% subplot(1,3,1);
% yyaxis left
plot([40, 80, 100, 120], mdeleted1(:, 1), '-d', [40, 80, 100, 120], mdeleted1(:, 2), ':d', [40, 80, 100, 120], mdeleted1(:, 3), '-.d', 'LineWidth', 1);
ylabel('Average no. of messages deleted per node','fontsize',12);
xlabel('No. of Repos in environment','fontsize',12);

% subplot(1,2,2);

lgd = legend('Pedestrian generated', 'Car generated', 'Bus generated');
lgd.FontSize = 12;
% subplot(1,3,2);
% bar(addr2, mdeleted2);
% title('500 cars in environment');
% xlabel('Mobile node address');
% subplot(1,3,3);
% bar(addr3, mdeleted3);
% title('1000 cars in environment');
% xlabel('Mobile node address');

% for repo = 1:144   
%     upspeeds5(repo, :) = [mean(SB5(:, repo)), mean(UB5(:, repo)), mean(PB5(:, repo))];
%     inspeeds5(repo, :) = mean(RI5(:, repo));
% end



% storage_30 = [S1(1:10000, 30), S2(1:10000, 30), S3(1:10000, 30), S4(1:10000, 30)];
% 
% figure
% plot(storage_30);
% xlabel('Time (s)','fontsize',12) 
% ylabel('Total storage used (messages stored)','fontsize',12)
% lgd = legend('40 repos', '80 repos', '100 repos', '120 repos');
% lgd.FontSize = 9;

% storage = zeros(3, c31);
% repos = 1:80;
% for repo = 1:80
% storage(:, repo) = [mean(S1(:, repo)); mean(S2(:, repo)); mean(S3(:, repo))];
% end
% 
% figure
% errorbar(repos,storage(2, :), storage(2, :)-storage(1, :), storage(3, :)-storage(2, :));
% xlabel('Repo No.','fontsize',12) 
% ylabel('Total storage used (messages stored)','fontsize',12)
% lgd = legend('40 repos', '80 repos', '100 repos', '120 repos');
% lgd.FontSize = 9;


% mdeleted = M3(10000, :)';
% figure
% bar(80:1134, mdeleted);
% title('No. messages deleted from mobile nodes','fontsize',16)
% xlabel('Node address','fontsize',12) 
% ylabel('No. messages deleted','fontsize',12)

% proc_upspeeds_30 = [PB1(1:10000, 30), PB2(1:10000, 30), PB3(1:10000, 30), PB4(1:10000, 30)];
% uproc_upspeeds_30 = [UB1(1:10000, 30), UB2(1:10000, 30), UB3(1:10000, 30), UB4(1:10000, 30)];
% static_upspeeds_30 = [SB1(1:10000, 30), SB2(1:10000, 30), SB3(1:10000, 30), SB4(1:10000, 30)];
% figure
% subplot(3,1,1);
% plot(uproc_upspeeds_30);
% title('Upload speeds for cloud offloading','fontsize',16)
% xlabel('Time (s)','fontsize',12) 
% ylabel('Bandwidth used (B)','fontsize',12)
% lgd = legend('40 repos', '80 repos', '100 repos', '120 repos');
% lgd.FontSize = 9;
% subplot(2,1,1);
% plot(proc_upspeeds_30);
% title('Upload speeds for processed messages','fontsize',16)
% xlabel('Time (s)','fontsize',12) 
% ylabel('Bandwidth used (B)','fontsize',12)
% lgd = legend('40 repos', '80 repos', '100 repos', '120 repos');
% lgd.FontSize = 9;
% subplot(2,1,2);
% plot(static_upspeeds_30);
% title('Upload speeds for unprocessed messages','fontsize',16)
% xlabel('Time (s)','fontsize',12) 
% ylabel('Bandwidth used (B)','fontsize',12)
% lgd = legend('40 repos', '80 repos', '100 repos', '120 repos');
% lgd.FontSize = 9;

% figure
% upspeeds1_30 = [SB3(:, 30) UB3(:, 30) PB3(:, 30)];
% subplot(1,2,1);
% bar(upspeeds1_30, 'stacked');
% lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload');
% lgd.FontSize = 9;
% xlabel('Time (s)','fontsize',12)  
% ylabel('Bandwidth used (B)','fontsize',12)
% inspeeds1_30 = RI3(:, 30);
% subplot(1,2,2);
% bar(inspeeds1_30);
% xlabel('Time (s)','fontsize',12)  
% ylabel('Bandwidth of input (B)','fontsize',12)


% Maybe could try an average over inspeeds (and upspeeds?) for the 
% different scenarios instead? Could potentially look better, like in the
% deleted mobile messages...

figure
% subplot(1,2,1);
yyaxis left
hold on
grid on
plot([40, 80, 100, 120], upspeeds1_30(:, 1), '-o', [40, 80, 100, 120], upspeeds1_30(:, 2), ':o', [40, 80, 100, 120], upspeeds1_30(:, 3), '-.o', 'LineWidth', 1);
plot([40, 80, 100, 120], inspeeds1_30, '-^', 'LineWidth', 1);
ylabel('Bandwidth of input (B)','fontsize',12)
lgd = legend();
lgd.FontSize = 10;
xlabel('No. of Repos in simulation','fontsize',12) 
ylabel('Bandwidth used (B)','fontsize',12)

yyaxis right
semilogy([40, 80, 100, 120], storages_30(:, 1), '-^', [40, 80, 100, 120], storages_30(:, 2), ':v', 'LineWidth', 1);
grid on
ylabel('Total storage used (B)','fontsize',12)
lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload', 'average input BW to repositories', 'non-processing message storage', 'processing message storage');
lgd.FontSize = 9;



% Maybe could try an average over storages for the different scenarios
% instead? Could potentially look better, like in the deleted mobile
% messages...
% for i = 1:10000
%     RS1_mean(i) = mean(RS1(i, :));
%     RP1_mean(i) = mean(RP1(i, :));
%     RS2_mean(i) = mean(RS2(i, :));
%     RP2_mean(i) = mean(RP2(i, :));
%     RS3_mean(i) = mean(RS3(i, :));
%     RP3_mean(i) = mean(RP3(i, :));
%     RS4_mean(i) = mean(RS4(i, :));
%     RP4_mean(i) = mean(RP4(i, :));
% end
% storages_30 = [mean(RS1_mean), mean(RP1_mean);
%                 mean(RS2_mean), mean(RP2_mean); 
%                 mean(RS3_mean), mean(RP3_mean);
%                 mean(RS4_mean), mean(RP4_mean)];
% figure
% bar([40, 80, 100, 120], storages_30, 'stacked');
% lgd = legend('non-processing message storage', 'processing message storage');
% lgd.FontSize = 9;
% xlabel('No. of Repos in simulation','fontsize',12)
% ylabel('Total storage used (B)','fontsize',12)

% upspeeds1_store = upspeeds1(1:40, 1)';
% upspeeds1_cloud = upspeeds1(1:40, 2)';
% upspeeds1_proc = upspeeds1(1:40, 3)';
% upspeeds4_store = upspeeds4(1:40, 1)';
% upspeeds4_cloud = upspeeds4(1:40, 2)';
% upspeeds4_proc = upspeeds4(1:40, 3)';
% upspeeds1_total = upspeeds1(1:40, 1)' + upspeeds1(1:40, 2)' + upspeeds1(1:40, 3)';
% upspeeds2_total = upspeeds2(1:40, 1)' + upspeeds2(1:40, 2)' + upspeeds2(1:40, 3)';
% upspeeds3_total = upspeeds3(1:40, 1)' + upspeeds3(1:40, 2)' + upspeeds3(1:40, 3)';
% upspeeds4_total = upspeeds4(1:40, 1)' + upspeeds4(1:40, 2)' + upspeeds4(1:40, 3)';
% 
% 
% figure
% plot(1:40, upspeeds1_store, 1:40, upspeeds1_cloud, 1:40, upspeeds1_proc, 1:40, upspeeds4_store, 1:40, upspeeds4_cloud, 1:40, upspeeds4_proc);
% title('Processing threads','fontsize',16)
% lgd =legend('non-processing message upload for 200', 'cloud offloading upload for 200', 'processed message upload for 200', 'non-processing message upload for 1000', 'cloud offloading upload for 1000', 'processed message upload for 1000');
% lgd.FontSize = 9;
% xlabel('Repository number','fontsize',12) 
% ylabel('Bandwidth used (B/s)','fontsize',12)


% figure
% 
% subplot(1,2,1);
% yyaxis left
% barhandle=bar(upspeeds1(:, 1:2), 'stacked');
% set(barhandle(1),'FaceColor',[1,0,0])
% set(barhandle(2),'FaceColor',[0,1,0])
% title('(a) Number of cars vs. up-link BW','fontsize',14)
% xlabel('Repository number','fontsize',12) 
% ylabel('Bandwidth used (B/s)','fontsize',12)
% ylim([0 5*10^6]);
% % xlim([17 57]);
% 
% yyaxis right
% plot(1:40, upspeeds1_total, 'r-+', 1:40, upspeeds2_total, '-*', 1:40, upspeeds3_total, 'm-o', 1:40, upspeeds4_total, 'g-X');
% lgd1 =legend('non-processing message upload', 'cloud offloading upload', 'message upload for 200', 'message upload for 400', 'message upload for 500', 'message upload for 1000');
% lgd1.FontSize = 9;
% ylabel('Bandwidth used (B/s)','fontsize',12)
% % xlim([17 57]);
% ylim([0 5*10^6]);
% 
% subplot(1,2,2);
% inspeeds = [inspeeds1(1:40, :), inspeeds2(1:40, :), inspeeds4(1:40, :)];
% bar_handle = bar(inspeeds);
% set(bar_handle(1),'FaceColor',[1,0,0])
% set(bar_handle(2),'FaceColor',[0,1,0])
% set(bar_handle(3),'FaceColor',[0,0,1])
% title('(b) Input Bandwidths','fontsize',14)
% lgd = legend('40 repos', '80 repos', '120 repos');
% lgd.FontSize = 9;
% xlabel('Repository number','fontsize',12) 
% ylabel('Bandwidth used (B/s)','fontsize',12)
% xlim([17 57]);

% repos = 1:80;
% figure
% hBarGrp=bar(abs(randn(320,2)),'grouped','stacked');  % 160 bars, group by 2
% off=hBarGrp(2).XOffset - 0.03;             % hidden property, offset from nominal x
% hBar1=bar(repos-off+0.03,upspeeds1(1:40, :),0.25,'stacked'); % draw first stacked as wanted
% lgd1 =legend('non-processing message upload', 'cloud offloading upload', 'processed message upload', 'message upload for 200', 'message upload for 500', 'message upload for 1000');
% lgd1.FontSize = 9;
% 
% hold all                            % hold axes, don't reset color order position
% hBar2=bar(repos+off-0.03,[nan(size(inspeeds1(1:40, :))) inspeeds1],0.30, 'r'); % second with place holder
% xlim([17 57]);
% 
% hold all                            % hold axes, don't reset color order position
% hBar3=bar(repos+2*off-0.03,[nan(size(inspeeds2(1:40, :))) inspeeds3],0.30, 'g'); % second with place holder
% xlim([17 57]);
% 
% hold all                            % hold axes, don't reset color order position
% hBar4=bar(repos+3*off-0.03,[nan(size(inspeeds4(1:40, :))) inspeeds6],0.30, 'b'); % second with place holder
% xlim([17 57]);


