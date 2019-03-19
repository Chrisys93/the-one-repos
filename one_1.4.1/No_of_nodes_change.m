% Make a function out of this, that takes the reports folder address and
% the number of runs as variables;

clear
M1 = dlmread('reports2_full_proc/MDMR1', ' ', 0, 2);
S1 = dlmread('reports2_full_proc/RAMR1', ' ', 0, 2);
PB1 = dlmread('reports2_full_proc/RPBWR1', ' ', 0, 2);
UB1 = dlmread('reports2_full_proc/RUPBWR1', ' ', 0, 2);
SB1 = dlmread('reports2_full_proc/RSBWR1', ' ', 0, 2);
RI1 = dlmread('reports2_full_proc/RISR1', ' ', 0, 2);
RP1 = dlmread('reports2_full_proc/RPrMR1', ' ', 0, 2);
RS1 = dlmread('reports2_full_proc/RSMR1', ' ', 0, 2);

M2 = dlmread('reports2_full_proc/MDMR2', ' ', 0, 2);
S2 = dlmread('reports2_full_proc/RAMR2', ' ', 0, 2);
UB2 = dlmread('reports2_full_proc/RUPBWR2', ' ', 0, 2);
PB2 = dlmread('reports2_full_proc/RPBWR2', ' ', 0, 2);
SB2 = dlmread('reports2_full_proc/RSBWR2', ' ', 0, 2);
RI2 = dlmread('reports2_full_proc/RISR2', ' ', 0, 2);
RP2 = dlmread('reports2_full_proc/RPrMR2', ' ', 0, 2);
RS2 = dlmread('reports2_full_proc/RSMR2', ' ', 0, 2);

M3 = dlmread('reports2_full_proc/MDMR3', ' ', 0, 2);
S3 = dlmread('reports2_full_proc/RAMR3', ' ', 0, 2);
PB3 = dlmread('reports2_full_proc/RPBWR3', ' ', 0, 2);
UB3 = dlmread('reports2_full_proc/RUPBWR3', ' ', 0, 2);
SB3 = dlmread('reports2_full_proc/RSBWR3', ' ', 0, 2);
RI3 = dlmread('reports2_full_proc/RISR3', ' ', 0, 2);
RP3 = dlmread('reports2_full_proc/RPrMR3', ' ', 0, 2);
RS3 = dlmread('reports2_full_proc/RSMR3', ' ', 0, 2);

M4 = dlmread('reports2_full_proc/MDMR4', ' ', 0, 2);
S4 = dlmread('reports2_full_proc/RAMR4', ' ', 0, 2);
PB4 = dlmread('reports2_full_proc/RPBWR4', ' ', 0, 2);
UB4 = dlmread('reports2_full_proc/RUPBWR4', ' ', 0, 2);
SB4 = dlmread('reports2_full_proc/RSBWR4', ' ', 0, 2);
RI4 = dlmread('reports2_full_proc/RISR4', ' ', 0, 2);
RP4 = dlmread('reports2_full_proc/RPrMR4', ' ', 0, 2);
RS4 = dlmread('reports2_full_proc/RSMR4', ' ', 0, 2);

M5 = dlmread('reports2_full_proc/MDMR5', ' ', 0, 2);
S5 = dlmread('reports2_full_proc/RAMR5', ' ', 0, 2);
UB5 = dlmread('reports2_full_proc/RUPBWR5', ' ', 0, 2);
PB5 = dlmread('reports2_full_proc/RPBWR5', ' ', 0, 2);
SB5 = dlmread('reports2_full_proc/RSBWR5', ' ', 0, 2);
RI5 = dlmread('reports2_full_proc/RISR5', ' ', 0, 2);
RP5 = dlmread('reports2_full_proc/RPrMR5', ' ', 0, 2);
RS5 = dlmread('reports2_full_proc/RSMR5', ' ', 0, 2);

M6 = dlmread('reports2_full_proc/MDMR6', ' ', 0, 2);
S6 = dlmread('reports2_full_proc/RAMR6', ' ', 0, 2);
PB6 = dlmread('reports2_full_proc/RPBWR6', ' ', 0, 2);
UB6 = dlmread('reports2_full_proc/RUPBWR6', ' ', 0, 2);
SB6 = dlmread('reports2_full_proc/RSBWR6', ' ', 0, 2);
RI6 = dlmread('reports2_full_proc/RISR6', ' ', 0, 2);
RP6 = dlmread('reports2_full_proc/RPrMR6', ' ', 0, 2);
RS6 = dlmread('reports2_full_proc/RSMR6', ' ', 0, 2);

% R6 = dlmread('proc_sims/RDMR6', ' ', 0, 1);
% M6 = dlmread('proc_sims/MDMR6', ' ', 0, 1);
% S6 = dlmread('proc_sims/RSMLR6', ' ', 0, 2);
[r21, c21] = size(M1);
[r22, c22] = size(M2);
[r31, c31] = size(S1);
[r32, c32] = size(S2);
maxstorM1 = zeros(c21, 0);
maxstor = zeros(c31, 0);
c3 = c32;

% for i = 1:r11
%     maxstorR1(i) = max(R1(i, :));
% end
% maxval1 = max(maxstorR1);

for i = 1:r21
    maxstorM1(i) = max(M1(i, :));
end
maxval2 = max(maxstorM1);

% figure
% hold on
% bar(reposfill);
% title('Repositories storage used according to storage depletion ratio');
% xlabel('Repo number');
% ylabel('Repo storage used (MB)');
% legend('2 msg/ 3 s', '2 msg/2 s', '2 msg/s', '1 msg/3 s', '1 msg/2 s', '1 msg/s');
% hold off

%figure
%bar3(M);

%figure
%bar3(R);

%figure
%bar3(S, 0.5);

% deleted_29 = [max(R1(:, 29)), max(R2(:, 29)), max(R3(:, 29)), max(R4(:, 29))];
% 
% figure
% plot([1 2 3 4], deleted_29);

mdeleted1 = [mean(M1(10800, 1:40)), mean(M1(10800, 41:240)), mean(M1(10800, 241:255));
             mean(M2(10800, 1:40)), mean(M2(10800, 41:340)), mean(M2(10800, 341:355));
             mean(M3(10800, 1:40)), mean(M3(10800, 41:440)), mean(M3(10800, 341:455));
             mean(M4(10800, 1:40)), mean(M4(10800, 41:540)), mean(M4(10800, 541:555));
             mean(M5(10800, 1:40)), mean(M5(10800, 41:840)), mean(M5(10800, 541:855));
             mean(M6(10800, 1:40)), mean(M6(10800, 41:1040)), mean(M6(10800, 1041:1055))];
% mdeleted2 = M2(10000, :);
% mdeleted3 = M3(10000, :);
% addr1 = 80:334;
% addr2 = 80:634;
% addr3 = 80:1134;

% Main output of this assessment (and these scenarios. Maybe modify to
% present better...? Either a line plot, or...associate with smth else?
figure
% subplot(1,3,1);
plot([200, 300, 400, 500, 800, 1000], mdeleted1(:, 1), '-^', [200, 300, 400, 500, 800, 1000], mdeleted1(:, 2), '-v', [200, 300, 400, 500, 800, 1000], mdeleted1(:, 3), '-d', 'LineWidth', 1);
lgd = legend('Pedestrian generated', 'Car generated', 'Bus generated');
lgd.FontSize = 12;
ylabel('Average no. of messages deleted per node','fontsize',12);
xlabel('No. of cars in environment','fontsize',12);
% subplot(1,3,2);
% bar(addr2, mdeleted2);
% title('500 cars in environment');
% xlabel('Mobile node address');
% subplot(1,3,3);
% bar(addr3, mdeleted3);
% title('1000 cars in environment');
% xlabel('Mobile node address');

for repo = 1:c3
    upspeeds1(repo, :) = [mean(SB1(:, repo)), mean(UB1(:, repo)), mean(PB1(:, repo))];
    upspeeds2(repo, :) = [mean(SB2(:, repo)), mean(UB2(:, repo)), mean(PB2(:, repo))];
    upspeeds3(repo, :) = [mean(SB3(:, repo)), mean(UB3(:, repo)), mean(PB3(:, repo))];
    upspeeds4(repo, :) = [mean(SB4(:, repo)), mean(UB4(:, repo)), mean(PB4(:, repo))];
    upspeeds5(repo, :) = [mean(SB5(:, repo)), mean(UB5(:, repo)), mean(PB5(:, repo))];
    upspeeds6(repo, :) = [mean(SB6(:, repo)), mean(UB6(:, repo)), mean(PB6(:, repo))];
    inspeeds1(repo, :) = mean(RI1(:, repo));
    inspeeds2(repo, :) = mean(RI2(:, repo));
    inspeeds3(repo, :) = mean(RI3(:, repo));
    inspeeds4(repo, :) = mean(RI4(:, repo));
    inspeeds5(repo, :) = mean(RI5(:, repo));
    inspeeds6(repo, :) = mean(RI6(:, repo));
end


% Need to check this one.
storage_30 = [S1(:, 30), S2(:, 30), S3(:, 30), S4(:, 30), S5(:, 30), S6(:, 30)];

figure
plot(storage_30, 'LineWidth', 1);
grid on
xlabel('Time (s)','fontsize',12) 
ylabel('Total storage used (messages stored)','fontsize',12)
lgd = legend('200 cars', '300 cars', '400 cars', '500 cars', '800 cars', '1000 cars');
lgd.FontSize = 9;

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
% lgd = legend('200 cars', '300 cars', '400 cars', '500 cars', '800 cars', '1000 cars');
% lgd.FontSize = 9;


% mdeleted = M3(10800, :)';
% figure
% bar(80:1134, mdeleted);
% title('No. messages deleted from mobile nodes','fontsize',16)
% xlabel('Node address','fontsize',12) 
% ylabel('No. messages deleted','fontsize',12)


%These shouldn't be influenced too much, so might not be VERY important,
%but nevertheless, might be at least marginally, so worth looking at/
% proc_upspeeds_30 = [PB1(:, 30), PB2(:, 30), PB3(:, 30), PB4(:, 30), PB5(:, 30), PB6(:, 30)];
% uproc_upspeeds_30 = [UB1(:, 30), UB2(:, 30), UB3(:, 30), UB4(:, 30), UB5(:, 30), UB6(:, 30)];
% static_upspeeds_30 = [SB1(:, 30), SB2(:, 30), SB3(:, 30), SB4(:, 30), SB5(:, 30), SB6(:, 30)];
% figure
% subplot(3,1,1);
% plot(uproc_upspeeds_30);
% title('Upload speeds for cloud offloading','fontsize',16)
% xlabel('Time (s)','fontsize',12) 
% ylabel('Bandwidth used (B)','fontsize',12)
% lgd = legend('200 cars', '300 cars', '400 cars', '500 cars', '800 cars', '1000 cars');
% lgd.FontSize = 9;
% subplot(2,1,1);
% plot(proc_upspeeds_30);
% title('Upload speeds for processed messages','fontsize',16)
% xlabel('Time (s)','fontsize',12) 
% ylabel('Bandwidth used (B)','fontsize',12)
% lgd = legend('200 cars', '300 cars', '400 cars', '500 cars', '800 cars', '1000 cars');
% lgd.FontSize = 9;
% subplot(2,1,2);
% plot(static_upspeeds_30);
% title('Upload speeds for unprocessed messages','fontsize',16)
% xlabel('Time (s)','fontsize',12) 
% ylabel('Bandwidth used (B)','fontsize',12)
% lgd = legend('200 cars', '300 cars', '400 cars', '500 cars', '800 cars', '1000 cars');
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


% Next two are very important and have to be merged in a way, to obtain the
% best analysis from them!
figure
upspeeds1_30 = [mean(SB1(:, 30)), mean(UB1(:, 30)), mean(PB1(:, 30));
                mean(SB2(:, 30)), mean(UB2(:, 30)), mean(PB2(:, 30)); 
                mean(SB3(:, 30)), mean(UB3(:, 30)), mean(PB3(:, 30));
                mean(SB4(:, 30)), mean(UB4(:, 30)), mean(PB4(:, 30));
                mean(SB5(:, 30)), mean(UB5(:, 30)), mean(PB5(:, 30)); 
                mean(SB6(:, 30)), mean(UB6(:, 30)), mean(PB6(:, 30))];
inspeeds1_30 = [mean(RI1(:, 30)),... 
                mean(RI2(:, 30)),...
                mean(RI3(:, 30)),...
                mean(RI4(:, 30)),...
                mean(RI5(:, 30)),...
                mean(RI6(:, 30))];
storages_30 = [mean(RS1(:, 30)), mean(RP1(:, 30));
                mean(RS2(:, 30)), mean(RP2(:, 30)); 
                mean(RS3(:, 30)), mean(RP3(:, 30));
                mean(RS4(:, 30)), mean(RP4(:, 30));
                mean(RS5(:, 30)), mean(RP5(:, 30)); 
                mean(RS6(:, 30)), mean(RP6(:, 30))];
% subplot(1,2,1);
yyaxis left
grid on
hold on
plot([200, 300, 400, 500, 800, 1000], upspeeds1_30, 'LineWidth', 1);
xlabel('No. of cars in simulation','fontsize',12) 
ylabel('Bandwidth used (B)','fontsize',12)
ylim([0 10*10^6]);
plot([200, 300, 400, 500, 800, 1000], inspeeds1_30, 'LineWidth', 1);
% set(bar_handle(1),'FaceColor',[0,0.5,1])
% set(bar_handle(2),'FaceColor',[0,1,0])
% set(bar_handle(3),'FaceColor',[0,1,0.5])
% subplot(1,2,2);
yyaxis right
semilogy([200, 300, 400, 500, 800, 1000], storages_30, 'LineWidth', 1);
ylabel('Bandwidth of input (B)','fontsize',12)
lgd = legend('non-processing message upload', 'cloud offloading upload', 'processed message upload', 'total data input', 'non-processing message storage', 'processing message storage');
lgd.FontSize = 10;


% These should be quite impacted.
% figure
% bar([200, 300, 400, 500, 800, 1000], storages_30, 'stacked');
% lgd = legend('non-processing message storage', 'processing message storage');
% lgd.FontSize = 9;
% xlabel('No. of cars in simulation','fontsize',12)
% ylabel('Total storage used (B)','fontsize',12)


upspeeds1_store = upspeeds1(:, 1)';
upspeeds1_cloud = upspeeds1(:, 2)';
upspeeds1_proc = upspeeds1(:, 3)';
upspeeds6_store = upspeeds6(:, 1)';
upspeeds6_cloud = upspeeds6(:, 2)';
upspeeds6_proc = upspeeds6(:, 3)';
upspeeds1_total = upspeeds1(:, 1)' + upspeeds1(:, 2)' + upspeeds1(:, 3)';
upspeeds3_total = upspeeds3(:, 1)' + upspeeds3(:, 2)' + upspeeds3(:, 3)';
upspeeds4_total = upspeeds4(:, 1)' + upspeeds4(:, 2)' + upspeeds4(:, 3)';
upspeeds6_total = upspeeds6(:, 1)' + upspeeds6(:, 2)' + upspeeds6(:, 3)';



% figure
% plot(1:80, upspeeds1_store, 1:80, upspeeds1_cloud, 1:80, upspeeds1_proc, 1:80, upspeeds6_store, 1:80, upspeeds6_cloud, 1:80, upspeeds6_proc);
% title('Processing threads','fontsize',16)
% lgd =legend('non-processing message upload for 200', 'cloud offloading upload for 200', 'processed message upload for 200', 'non-processing message upload for 1000', 'cloud offloading upload for 1000', 'processed message upload for 1000');
% lgd.FontSize = 9;
% xlabel('Repository number','fontsize',12) 
% ylabel('Bandwidth used (B/s)','fontsize',12)


% Important! See how it may be compressed!
figure
% subplot(1,2,1);
yyaxis left
grid on
barhandle=bar(upspeeds1(:, 1:2), 'stacked');
set(barhandle(1),'FaceColor',[1,0,0])
set(barhandle(2),'FaceColor',[0,1,0])
% title('Number of cars vs. up-link BW vs. input BW','fontsize',14)
xlabel('Repository number','fontsize',12) 
ylabel('Bandwidth used (B/s)','fontsize',12)
ylim([0 10*10^6]);
xlim([17 48]);

yyaxis right
plot(1:80, upspeeds1_total, 'b-+', 1:80, upspeeds3_total, 'g-o', 1:80, upspeeds4_total, 'm-X', 1:80, upspeeds6_total, 'y-*', 1:80, inspeeds1, '-+', 1:80, inspeeds3, '-o', 1:80, inspeeds4, '-X', 1:80, inspeeds6, '-*', 'LineWidth', 1);
lgd1 =legend('non-processing message upload', 'cloud offloading upload', 'message upload for 200', 'message upload for 400', 'message upload for 500', 'message upload for 1000', 'message input for 200 cars', 'message input for 400 cars', 'message input for 500 cars', 'message input for 1000 cars', 'Location', 'southoutside');
lgd1.FontSize = 9;
% lgd1.NumColumns = 3;
ylabel('Bandwidth used (B/s)','fontsize',12)
xlim([17 48]);
ylim([0 10*10^6]);

% subplot(1,2,2);
% bar_handle = bar(inspeeds);
% set(bar_handle(1),'FaceColor',[1,0,0])
% set(bar_handle(2),'FaceColor',[0,1,0])
% set(bar_handle(3),'FaceColor',[0,0,1])
% title('(b) Input Bandwidths','fontsize',14)
% lgd = legend('200 cars', '500 cars', '1000 cars');
% lgd.FontSize = 9;
% xlabel('Repository number','fontsize',12) 
% ylabel('Bandwidth used (B/s)','fontsize',12)
% xlim([17 48]);

% repos = 1:80;
% figure
% hBarGrp=bar(abs(randn(320,2)),'grouped','stacked');  % 160 bars, group by 2
% off=hBarGrp(2).XOffset - 0.03;             % hidden property, offset from nominal x
% hBar1=bar(repos-off+0.03,upspeeds1,0.25,'stacked'); % draw first stacked as wanted
% 
% hold all                            % hold axes, don't reset color order position
% hBar2=bar(repos+off-0.03,[nan(size(inspeeds1)) inspeeds1],0.30, 'r'); % second with place holder
% xlim([17 48]);
% 
% hold all                            % hold axes, don't reset color order position
% hBar3=bar(repos+2*off-0.03,[nan(size(inspeeds3)) inspeeds3],0.30, 'g'); % second with place holder
% xlim([17 48]);
% 
% hold all                            % hold axes, don't reset color order position
% hBar4=bar(repos+3*off-0.03,[nan(size(inspeeds6)) inspeeds6],0.30, 'b'); % second with place holder
% 
% lgd1 =legend('non-processing message upload', 'cloud offloading upload', 'processed message upload', 'message upload for 200', 'message upload for 500', 'message upload for 1000');
% lgd1.FontSize = 9;
% xlim([17 48]);


